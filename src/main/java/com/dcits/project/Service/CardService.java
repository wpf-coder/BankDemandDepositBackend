package com.dcits.project.service;

import com.dcits.internal.flowHandle.BaseFlowHandle;
import com.dcits.project.common.NormalMassage;
import com.dcits.project.common.Util;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.mapper.CardMapper;
import com.dcits.project.mapper.UserMapper;
import com.dcits.project.pojo.BankCardSerial;
import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.TransactionFlow;
import com.dcits.project.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CardService {
    @Autowired
    private CardMapper mapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TransactionFlowService transactionFlowService;

    @Autowired
    private CardService self;

    /**
     * 返回一个本行银行卡的前缀
     * @how:
     *      目前只返回 静态返回唯一值。
     * @return:
     *       java.lang.String
     **/
    public String getOneSelfBankSerialPrefix(){
        return BankCardSerial.SELF_BANK_PREFIX;
    }


    public boolean isSelfBank(String bankCard){
        return bankCard.substring(0,6).equals(getOneSelfBankSerialPrefix());
    }




    //*************************************对外接口*****************************//

    /**
     * 根据银行卡卡号查询银行卡信息。该服务仅对向柜员，客户渠道暂时不允许访问。
     * @how:
     *      直接从数据库中查询。
     * @return:
     *       com.dcits.project.pojo.Card
    **/
    public Card queryCardInformation(String cardId){
        if(Card.validateCardId(cardId))
            return mapper.selectByPrimaryKey(cardId);
        return null;
    }

    /**
     * 这个函数到底为什么存在？
     * @param card
     * @return
     */
    public Card queryCardInfById(Card card){
        if(card.getId()==null
                && !Card.validateCardId(card.getId())
                && card.getUserId() ==null)
            throw new RequestHandleException("后台参数调用错误");
        return mapper.selectByFeature(card);
    }

    /**
     * 网上银行查询账号余额的接口;客户接口
     * @how:
     *      现根据 type 判断查询方式，
     *      再进行核实，是否与controller传过来session内的用户信息匹配，
     *      若不匹配，则 抛出只能查询自己信息的错误。
     *      否则 根据查询方式关联查询。
     * @return:
     *       com.dcits.project.pojo.Card
    **/
    public List<Card> queryCardInformation(String phoneOrIDCard, int type, User compareUser){
        String cardId = null;
        User user = new User();

        //若 用户输入的信息 与 session 中的数据一致，那么继续。
        if((type == 0 && compareUser.getId().equals(phoneOrIDCard))
                || (type == 1 && compareUser.getPhone().equals(phoneOrIDCard))){
            if(type == 0)
                user.setId(phoneOrIDCard);
            else
                user.setPhone(phoneOrIDCard);
        }
        else
            throw new RequestHandleException(NormalMassage.ERROR_STATE,"仅支持检索自己卡的信息");

        return mapper.selectByUserInformation(user);
    }

    /**
     * 网上银行查询账号余额的接口;客户接口
     * @how:
     *      根据session中信息查询card信息
     * @return:
     *       com.dcits.project.pojo.Card
     **/
    public Card queryCardInformation(User user){
        if(mapper.selectByUserInformation(user)==null)
        {
            throw new RequestHandleException("用户信息不匹配，无法返回信息");
        }
        Card card=mapper.selectByUserInformation(user).get(0);
        if(card==null){
            throw new RequestHandleException("用户信息不匹配，无法返回信息");
        }
        return card;
    }


    /**
     * 存款业务实现。
     * @return:
     *       boolean
    **/
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean deposit(String cardId, BigDecimal amount,String branchSerial){
        TransactionFlow flow = cashTransaction(cardId,amount,branchSerial,BaseFlowHandle.deposit,null);
        return flow != null;
    }

    /**
     * 取款业务实现。
     * @return:
     *       boolean
     **/
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionFlow withdraw(String cardId, BigDecimal amount,String branchSerial,String password){
        TransactionFlow flow = cashTransaction(cardId,amount,branchSerial,BaseFlowHandle.withdrawal,password);
        return flow;

    }

    /**
     * 转账接口。
     * @how:
     * @Params:
     *      flow。 以汇款人角度生成一个flow，仅 交易编号与余额字段可以省略。
     * @return: 
     *       boolean
    **/
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean transfer(String payerCardId,String payeeCardId,BigDecimal amount ,String password,String name){
        if(!isSelfBank(payeeCardId)
                ||!isSelfBank(payerCardId))
            throw new RequestHandleException(NormalMassage.ERROR_STATE,"该银行卡是外行银行卡，暂时不支持");
        User counterparty = new User();


        Card payer = mapper.selectByPrimaryKey(payerCardId);
        Card payee = mapper.selectByPrimaryKey(payeeCardId);

        counterparty.setName(name);
        // 检查收款人 是否存在
        if(payee == null)
            throw new RequestHandleException("收款人不存在");
        //其银行卡账户与 名字是否对应
        counterparty.setId(payee.getUserId());
        if(userMapper.selectByFeature(counterparty)==null)
            throw new RequestHandleException("收款人名字与账号信息不一致！");

        //检查付款人账户信息
        if(payer == null)
            throw new RequestHandleException("请正确输入你的银行卡不存在");
        if(payer.getBalance().subtract(amount).compareTo(new BigDecimal(0)) == -1)
            throw new RequestHandleException("当前余额小于转账额度");
        if(!payer.getPassword().equals(Util.md5(password)))
            throw new RequestHandleException("交易密码错误");


        //付款人减少交易额
        payer.setBalance(payer.getBalance().subtract(amount));
        //收款人增加收益额
        payee.setBalance(payee.getBalance().add(amount));
        //更新银行卡
        mapper.updateByPrimaryKey(payer);
        mapper.updateByPrimaryKey(payee);


        //记录流水
        BaseFlowHandle flowHandle = BaseFlowHandle.getInstance(BaseFlowHandle.remittance);
        flowHandle.setSubjectBalance(payer.getBalance())
                .setSubjectCardNumber(payerCardId)
                .setCounterpartyCardNumber(payeeCardId)
                .setAmount(amount)
                .recordFlow(transactionFlowService);


        //交换主体,即更换 主体的卡号、余额，交易对象，交易类型，并记录流水
        flowHandle.changeType(BaseFlowHandle.receive_Payment)
                .setSubjectBalance(payee.getBalance())
                .setSubjectCardNumber(payeeCardId)
                .setCounterpartyCardNumber(payerCardId)
                .recordFlow(transactionFlowService);

        return true;
    }


    //**********************************内部实现************************************//


    /**
     * 本行对外进行的现金交易业务的实现。调用者必须处于事务管理当中，且等级为 可重读。
     * @how:
     *       在事务开始即查询银行卡信息表、锁表前，准备好流水记录的确定数据，以提高效率。
     *       但目前现在有个问题，银行本身的银行卡前并未减少。
     * @return:
     *       boolean
    **/
    private TransactionFlow cashTransaction(String cardId, BigDecimal amount,String branchSerial,int type,String password){

        //事务开始前预先准备流水记录的部分信息，提高并发效率
        BaseFlowHandle flowHandle = BaseFlowHandle.getInstance(type);
        flowHandle.setAmount(amount)
                .setBranchId(branchSerial)
                .setSubjectCardNumber(cardId);

        //开始事务
        Card card = mapper.selectByPrimaryKey(cardId);
        if(card == null) {
            throw new RequestHandleException("该银行卡不存在");
        }
        //根据交易类型进行 加减判断
        if(type == BaseFlowHandle.deposit){
            //如果是存款，即加
            card.setBalance(card.getBalance().add(amount));
        }else if(type == BaseFlowHandle.withdrawal){
            //如果是取款，即减
            if(!card.getPassword().equals(Util.md5(password)))
                throw new RequestHandleException("交易密码错误");
            if(card.getBalance().compareTo(amount) < 0)
                throw new RequestHandleException("取款额不能大于余额，现有余额为:"+card.getBalance());
            card.setBalance(card.getBalance().subtract(amount));
        }

        this.mapper.updateByPrimaryKey(card);

        //记录交易流水
        return flowHandle.setSubjectBalance(card.getBalance())
                .recordFlow(this.transactionFlowService);
    }


}

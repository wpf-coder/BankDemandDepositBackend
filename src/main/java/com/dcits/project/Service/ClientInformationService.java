package com.dcits.project.service;

import com.alibaba.fastjson.JSONObject;
import com.dcits.project.common.CardIdUtil;
import com.dcits.project.common.Util;
import com.dcits.project.dto.UserEstablish;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.mapper.CardMapper;
import com.dcits.project.mapper.EbankUserMapper;
import com.dcits.project.mapper.UserMapper;
import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.EbankUser;
import com.dcits.project.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

@Service
@Transactional
public class ClientInformationService {
    @Autowired
    CardService cardService;

    @Autowired
    UserMapper userMapper;
    @Autowired
    EbankUserMapper ebankUserMapper;
    @Autowired
    CardMapper cardMapper;

    /**
     * 验证身份证和手机号是否已经注册
     * @how:
     * @Param
     *      Id:     身份证号码
     *      num:    电话号码
     * @return:
     *
     **/
    //question: 当两个都为空的时候，该函数的用途是什么？ controller不需要检测吗？
    public int idValidation(String Id, String num) {
        if(!Id.equals("")){
            if(userMapper.selectByPrimaryKey(Id)!=null){
                throw new RequestHandleException(1,"该身份证号码已开户");
            }
        }
        if(!num.equals("")){
            System.out.println(userMapper.selectByPhone(num));
            if(userMapper.selectByPhone(num).size()!=0){
                //已存在手机号码
                throw new RequestHandleException(1,"该手机号码已开户");
            }
        }
        return 0;
    }

    /**
     * 手机号验证码发送验证
     * 验证其是否开通网银
     * @param phone
     * @return
     *      0  验证成功
     *      参数为空或为开通网银
     */
    public int phoneEbankValidation(String phone) {
        if(!phone.equals("")){
            User user=userMapper.selectByPhone(phone).get(0);
            if(user!=null&&user.getEbankState()==User.OPEN_EBANK_STATE){//可以查询到user且已开通网银
                return 0;
            }
            throw new RequestHandleException("手机号码未开通网银！");
        }
        throw new RequestHandleException("参数异常！");
    }

    /**
     * 开通网上银行
     */
    private void establishEbankAccount(EbankUser ebankUser) {
        ebankUserMapper.insert(ebankUser);
    }

    /**
     * 开户业务
     * 首先验证开户的身份证号码和手机号
     * 插入用户信息
     * 生成银行卡信息并插入
     * 检测是否开通网银
     */
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public Map<String,Object> establishAccount(UserEstablish userEstablish) {

        JSONObject result=new JSONObject();

        //验证身份证码或手机号是否已经存在
        if(idValidation(userEstablish.getId(),userEstablish.getPhone())==1){
            //已存在则开户失败！
            throw new RequestHandleException("该用户已开户");
        }
        //不存在  获取user数据并插入用户信息表
        User user=new User(
                userEstablish.getId(),
                userEstablish.getName(),
                userEstablish.getPhone(),
                userEstablish.getAddress(),
                userEstablish.getEmail(),
                userEstablish.getEbankState(),
                userEstablish.getMobileBankState(),
                userEstablish.getForeignExchangeState());

        userMapper.insert(user);
        //System.out.println(user);

        Card card=new Card();
        /**
         * 生成卡号，将卡号和用户身份证插入银行卡信息表
         *           卡号总共19位，前6位为前缀 888888
         * 生成银行卡号后，并验证是否重复卡号，重复则重新生成并验证，直至验证通过
         */
        String cardId;
        //循环随机生成银行卡号，直至新卡号与数据库已存在卡不冲突。
        cardId= CardIdUtil.getBankNumber(cardService.getOneSelfBankSerialPrefix());
        while (cardMapper.selectByPrimaryKey(cardId)!=null){
            cardId=CardIdUtil.getBankNumber(cardService.getOneSelfBankSerialPrefix());
        }

        card.setId(cardId);
        card.setUserId(userEstablish.getId());
        card.setState(Card.NORMAL_CARD_STATE);
        card.setBalance(new BigDecimal(0));
        card.setLastTime(new Timestamp(new java.util.Date().getTime()));
        //银行卡密码加密
        card.setPassword(Util.md5(userEstablish.getCardPwd()));
        cardMapper.insert(card);
        System.out.println(card);

        //查验是否开通网银
        if(user.getEbankState()==User.OPEN_EBANK_STATE){

            String ebankPwd=EbankUser.getInitPassword(user);

            EbankUser ebankUser=new EbankUser(
                    userEstablish.getId(),
                    Util.md5(ebankPwd),
                    EbankUser.OFFLINE_STATE);
            System.out.println(ebankUser);
            result.put("ebankPwd",ebankPwd);
            establishEbankAccount(ebankUser);
        }
        result.put("cardId",cardId);
        return result;
    }

     /**
      * 用户信息查询
      * @how:
      *     输入身份证号码
      *     查询user 及 card 信息
     **/
    public Map<String,Object> infoQuery(String id){
        JSONObject map=new JSONObject();
        if(id==null||id.equals("")){
            throw new RequestHandleException("身份证号码参数有误");
        }
        User user= userMapper.selectByPrimaryKey(id);
        if(user==null){
            throw new RequestHandleException("身份证号码未开户");
        }
        map.put("user",user);
        Card card= cardMapper.selectByUserInformation(user).get(0);
        if(card==null){
            throw new RequestHandleException("银行卡信息查询失败");
        }
        map.put("card",card);
        return map;
    }

    /**
     * 开通业务  网银 手机银行  外汇
     * @param map
     *      Id - 身份证号
     *      cardId - 银行卡号
     *      ebank_state  1开通0不开通网银状态
     *      mobile_bank_state手银状态
     *      foreign_ exchange_state外汇状态
     * @return
     */
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public Map<String,Object> serviceOpen(JSONObject map){

        JSONObject result=new JSONObject();
        //验证参数
        if(map.getString("Id")==null||map.getString("Id").equals("")
                ||map.getString("cardId")==null||map.getString("cardId").equals("")
                ||map.getInteger("ebank_state")==null
                ||map.getInteger("mobile_bank_state")==null
                ||map.getInteger("foreign_exchange_state")==null){
            throw new RequestHandleException("参数有误");
        }

        //根据身份证号查询user
        User user=userMapper.selectByPrimaryKey(map.getString("Id"));
        if(user==null){
            throw new RequestHandleException("未开户");
        }

        //user未开通网银，且要开通网银
        if(map.getInteger("ebank_state")==User.OPEN_EBANK_STATE
                &&user.getEbankState()==User.NON_OPEN_EBANK_STATE){
            //重置初始化密码
            String ebankPwd="@"+user.getId().substring(user.getId().length()-6)
                    +"a"+user.getPhone().substring(user.getPhone().length()-6);
            //判断是否开过网银  开通过则直接初始化密码
            if(ebankUserMapper.selectByPrimaryKey(user.getId())!=null){
                EbankUser ebankUser=ebankUserMapper.selectByPrimaryKey(user.getId());
                //初始化密码
                ebankUser.setPassword(Util.md5(ebankPwd));
                result.put("ebankPwd",ebankPwd);
                ebankUserMapper.updateByPrimaryKey(ebankUser);
            }
            else { //未开通过，则创建新的网银账户
                EbankUser ebankUser=new EbankUser(
                        user.getId(),
                        Util.md5(ebankPwd),
                        EbankUser.OFFLINE_STATE);
                System.out.println(ebankUser);
                result.put("ebankPwd",ebankPwd);
                establishEbankAccount(ebankUser);
            }

        }
        //更新业务开通状态
        user.setEbankState(map.getInteger("ebank_state"));
        user.setMobileBankState(map.getInteger("mobile_bank_state"));
        user.setForeignExchangeState(map.getInteger("foreign_exchange_state"));
        userMapper.updateByPrimaryKey(user);
        return result;

    }

    /**
     * 验证银行卡和身份证号码是否匹配 匹配则返回用户信息user
     * @param id 身份证号码
     * @param cardId 银行卡号
     * @return
     */
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public User valicationIDandCardId(String id,String cardId){
        User user=userMapper.selectByPrimaryKey(id);
        Card card= cardService.queryCardInformation(cardId);
        if(user==null||card==null){
            throw new RequestHandleException("用户信息有误！");
        }
        else{
            if(user.getId().equals(id)){
                return  user;
            }else {
                throw new RequestHandleException("身份证号码和银行卡号不匹配");
            }

        }

    }

    /**
     * 网银密码修改   柜员接口
     * @param id 身份证号码
     * @param cardId 银行卡号
     * @return
     */
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public int updateEbankUserPwd(String id,String cardId,String pwd){
        Card card= cardService.queryCardInformation(cardId);
        if(card!=null&&card.getUserId().equals(id)){
            EbankUser ebankUser=ebankUserMapper.selectByPrimaryKey(id); //获取身份证号码对应网银账户
            if(ebankUser!=null){
                //检测新旧密码是否相同
                if(Util.md5(pwd).equals(ebankUser.getPassword())){
                    throw new RequestHandleException("新密码不能跟旧密码相同！");
                }
                ebankUser.setPassword(Util.md5(pwd)); //修改密码
                //更新数据库
                ebankUserMapper.updateByPrimaryKey(ebankUser);
                return 0;
            }
            else {
                throw  new RequestHandleException("网银未开通！");
            }
        }
        throw  new RequestHandleException("身份信息不匹配，请拿本人身份证修改密码");
    }

    /**
     * 银行卡密码修改  柜员接口
     * @param id 身份证号码
     * @param cardId 银行卡号
     * @return
     */
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public int updateCardPwd(String id,String cardId,String pwd){
        Card card= cardService.queryCardInformation(cardId);
        if(card!=null&&card.getUserId().equals(id)){
            //检测新旧密码是否相同
            if(Util.md5(pwd).equals(card.getPassword())){
                throw new RequestHandleException("新密码不能跟旧密码相同！");
            }
            card.setPassword(Util.md5(pwd));//修改密码
            //更新数据库
            cardMapper.updateByPrimaryKey(card);
            return 0;
        }
        throw  new RequestHandleException("身份信息不匹配，请拿银行卡本人身份证修改密码");
    }

     /**
      * 重置初始密码    客户接口
      * @how:
      * @Param
      *       id 身份证号码
      *       pwd 新密码
     **/
     @Transactional(rollbackFor = Exception.class,isolation = Isolation.REPEATABLE_READ)
    public int resetEbankPwd(String id,String pwd){
        EbankUser ebankUser=ebankUserMapper.selectByPrimaryKey(id); //获取身份证号码对应网银账户
        //检测新旧密码是否相同
        if(Util.md5(pwd).equals(ebankUser.getPassword())){
            throw new RequestHandleException("新密码不能跟旧密码相同！");
        }
        ebankUser.setPassword(Util.md5(pwd)); //修改密码
        //更新数据库
        ebankUserMapper.updateByPrimaryKey(ebankUser);
        return 0;
    }
}
package com.dcits.project.timing;



import com.dcits.internal.flowHandle.BaseFlowHandle;
import com.dcits.project.mapper.*;
import com.dcits.project.pojo.*;
import com.dcits.project.service.TransactionFlowService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

@Component
@Transactional
@EnableScheduling
public class InterestHandle {
    @Autowired
    private InterestHandle self;                            //使用该代理以保证事务能正常管理
    //****************配置参数区域*******************//
    @Resource
    private InterestHandle.Config config;

    @Configuration
    @ConfigurationProperties("system.timing.settlement")
    @Setter
    @Getter
    public static class Config{
        private List<Integer> month;
        private int day;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"month\":")
                    .append(month);
            sb.append(",\"day\":\"")
                    .append(day).append('\"');
            sb.append('}');
            return sb.toString();
        }
    }

    //************************service district*************//
    @Autowired
    private TransactionFlowService transactionFlowService;

    //***********************mapper区域********************//
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private InterestRateMapper interestRateMapper;
    @Autowired
    private DemandInterestMapper demandInterestMapper;

    @Autowired
    private SettlementInterestLogMapper settlementInterestLogMapper;

    //************************方法区**********************//
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRES_NEW)
    public void handleInterest(Calendar calendar){
        //从 mapper中获取所有银行卡信息
        List<Card> cards = cardMapper.selectAllCardLinkDemandInterest();

        //累计计息
        startAccumulateDemandInterest(calendar.getTime());

        //判断是否应该结息
        if(calendar.get(Calendar.DAY_OF_MONTH) == config.getDay()){
            Iterator<Integer> iterator = config.getMonth().iterator();
            int month = calendar.get(Calendar.MONTH) + 1;
            while(iterator.hasNext()){
                if(iterator.next() == month){
                    startSettleDemandInterest(cards,calendar.getTime());
                    break;
                }
            }
        }
    }


    /**
     * 开始累计 用户的累计计息数，该定时任务优先于 结息任务
     * @how:
     *      从 mapper中获取所有银行卡信息，该银行卡信息应该
     *      附上其计息信息。
     *      遍历所有的银行卡，调用accumulateOneAccount
     *      并判断返回值，若false则错误处理一下,错误处理
     *      暂时简单处理。
     * @return:
     *       void
     **/
    public void startAccumulateDemandInterest(Date systemTime){
        List<Card> cards = cardMapper.selectAllCardLinkDemandInterest();
        //遍历所有的银行卡，调用accumulateOneAccount
        for(Card card : cards){
            // 失败处理
            if(!self.accumulateOneAccount(card,systemTime)){

            }
        }
    }


    /**
     * 累计计息一个银行卡的累计计息数。单个银行卡为一个事务。
     * @how:
     *      新的累计计息数 = 旧的累计计息数  + 银行卡余额
     *      更新上一次 累计时间。
     * @return:
     *       boolean
     **/
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRES_NEW)
    public boolean accumulateOneAccount(Card card,Date systemTime) {
        DemandInterest demandInterest = card.getDemandInterest();
        if(demandInterest == null){
            demandInterest = new DemandInterest();
            demandInterest.setCardId(card.getId());
            demandInterest.setCumulativeInterest(new BigDecimal(0));
            demandInterest.setLastSettleTime(systemTime);
        }

        //更新计息数 算法=  新的累计计息数 = 旧的累计计息数  + 银行卡余额
        demandInterest.setCumulativeInterest(demandInterest.getCumulativeInterest().add(card.getBalance()));
        //更新上一次 累计时间 为系统当前时间。
        demandInterest.setLastCountTime(systemTime);
        //存入数据库。
        if(card.getDemandInterest() == null)
            demandInterestMapper.insert(demandInterest);
        else
            demandInterestMapper.updateByPrimaryKey(demandInterest);

        //若 之前银行卡无 累计计息记录，则现在绑定，以免之后结息错误。
        card.setDemandInterest(demandInterest);
        return true;
    }

    /**
     * 每季度结息期定时启动，以该期的利息结算，并将利息加入用户余额。
     * 优先级低于 每日累计。
     * @how:
     *      从 mapper中获取所有银行卡信息，该银行卡信息应该
     *      附上其计息信息。
     *      再从系统中查到 当期的利率。
     *      遍历所有的银行卡，调用 startSettleDemandInterest
     *      并判断返回值，若false则错误处理一下,错误处理
     *      暂时简单处理。
     * @return:
     *       void
     **/
    public void startSettleDemandInterest(List<Card> cards,Date systemTime){
        System.out.println("--------------------------------------------InterestHandle.startSettleDemandInterest");

        //获取 活期年 利率
        InterestRate  demandInterestRate = interestRateMapper.selectByCodeAndTime(InterestRate.DEMAND_INTEREST_RATE,systemTime);
        //计算日利率
        BigDecimal dayInterest = demandInterestRate.getValue().divide(new BigDecimal(demandInterestRate.getBaseTime()),
                new MathContext(5));

        //遍历所有的银行卡，调用accumulateOneAccount
        for(Card card : cards){
            // 失败处理
            if(!self.settleOneDemandInterest(card,dayInterest,systemTime)){

            }
        }

    }


    /**
     * 计算该银行卡一次季度的利息数，并加入银行卡的余额，并更新累计计息信息，
     * 另外存储该银行卡的结息信息记录到db中。
     * @how:
     *
     * @return:
     *       boolean
     *          如果事务失败，则返回false
     **/
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRES_NEW)
    public boolean settleOneDemandInterest(Card card, BigDecimal interestRate,Date systemTime){
        //获得累计计息数
        DemandInterest demandInterest = card.getDemandInterest();
        //interest 结息额 = 累计计息 * interestRate
        BigDecimal interest = demandInterest.getCumulativeInterest().multiply(interestRate);
        //银行卡余额 += interest
            //该部分应该需要四舍五入，然后记录 多给的部分与少给的部分。
            //****************************缺陷区域***********************//
        card.setBalance(card.getBalance().add(interest));
        //存储该银行卡信息
        cardMapper.updateByPrimaryKey(card);

        //记录结息记录、
        logSettlementInterest(card, systemTime, demandInterest, interest);


        //记录流水转结。
        BaseFlowHandle flowHandle = BaseFlowHandle.getInstance(BaseFlowHandle.interest_Settlement);
        flowHandle.setAmount(interest)
                .setSubjectCardNumber(card.getId())
                .setSubjectBalance(card.getBalance())
                .recordFlow(this.transactionFlowService);



        //更新 累计计息的余额，将其置为0；
            //对应的这里需要将剩余的额度进行更新。
        demandInterest.setCumulativeInterest(new BigDecimal(0));
        //并且将上次结息时间更新。
        demandInterest.setLastSettleTime(systemTime);
        //更新累计计息
        demandInterestMapper.updateByPrimaryKey(demandInterest);


        return true;
    }



    private void logSettlementInterest(Card card, Date systemTime, DemandInterest demandInterest, BigDecimal interest) {
        SettlementInterestLog settlementInterestLog = new SettlementInterestLog();
        //以 card 中的字段填充相应的 settlement中的字段
        settlementInterestLog.setCardId(card.getId());
        //将 结息的累计计息 用 demandInterest 字段更新
        settlementInterestLog.setCumulativeInterest(demandInterest.getCumulativeInterest());
        settlementInterestLog.setLastTime(demandInterest.getLastSettleTime());
        //将 结息额 与当天时间 setter 进 settlement
        settlementInterestLog.setInterest(interest);
        settlementInterestLog.setTime(systemTime);
        //存储settlement
        settlementInterestLogMapper.insert(settlementInterestLog);
    }
}
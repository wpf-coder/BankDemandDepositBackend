package com.dcits.project.timing;


import com.dcits.project.mapper.LedgerBalanceMapper;
import com.dcits.project.mapper.LedgerRecordMapper;
import com.dcits.project.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.List;

/**
 * 该类用来 每天 下午五点，统计网点的现金日流量。
 * @version: 1.0
**/
@Component
@EnableTransactionManagement
@EnableScheduling
public class LedgerFlowHandle {



    @Autowired
    private LedgerRecordMapper recordMapper;
    @Autowired
    private LedgerBalanceMapper balanceMapper;

    private static final BigDecimal ZERO_PROTOTYPE = new BigDecimal(0);
    private static final MathContext MATH_CONTEXT = new MathContext(8);

    private LedgerRecord prototypeOfLedgerRecord = new LedgerRecord();


    /**
     * 定时触发，统计总账流水，以此更新总账余额以及总账报表。
     * @how:
     *      通过存款、取款业务类型 和  交易主体为银行自身银行卡号 的条件检索数据库中的流水记录，
     *      然后调用 统计一个网点的流水方法，来统计网点该天的现金流水。
     *
     *      下一个系统的需求，检测其是否以及更新，查询语句应该涉及到网点是否以下班。
     * @return:
     *       void
    **/
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRES_NEW)
    public void updateBalanceOfLedger(Calendar systemTime) {
        List<LedgerBalance> ledgerBalances = balanceMapper.
                getLedgerAndBranchCorrespondingLedgerFLow(systemTime.getTime(), 0);

        for (LedgerBalance one : ledgerBalances) {
            BigDecimal balance = countBalanceBySummarizeFlow(systemTime, LedgerFlowHandle.MATH_CONTEXT,
                    LedgerFlowHandle.ZERO_PROTOTYPE, one);

            if(isaNewBalance(one)){
                one.setBalance(balance);
                one.setState(LedgerBalance.OPEN_STATE);
                balanceMapper.insertDefaultValue(one);
            }else {
                //备份一下前一期的余额。
                balanceMapper.backupOneRecord(one, systemTime.getTime());

                //更新余额，并更新数据库。
                one.setBalance(balance);
                balanceMapper.updateByPrimaryKey(one);
            }
        }
    }

    private boolean isaNewBalance(LedgerBalance one) {
        return one.getAccount() == null;
    }


    private BigDecimal countBalanceBySummarizeFlow(Calendar systemTime, MathContext mathContext, BigDecimal zero, LedgerBalance one) {
        BigDecimal debit = new BigDecimal(0);
        BigDecimal credit = new BigDecimal(0);
        final BigDecimal nextBalance;

        //计算期中的借贷发生额。
        for (LedgerFlow flow : one.getFlows()) {
            if (flow.getLoadRelationship() == Subject.CREDIT) {
                credit = credit.add(flow.getAmount(), mathContext);
            } else {
                debit = debit.add(flow.getAmount(), mathContext);
            }
        }
        //记录总账报表record
        nextBalance = recordCurrentLedgerRecord(systemTime, one, debit, credit);

        return nextBalance;
    }

    private BigDecimal recordCurrentLedgerRecord(Calendar systemTime, LedgerBalance one, BigDecimal debit, BigDecimal credit) {
        BigDecimal nextBalance;
        LedgerRecord record = getLedgerRecordByLastBalance(systemTime, one);

        //初始期初、末余额为 0
        setZeroAmountOfBeginAndEnd(record);

        //根据 one是否是此次新加的 账户余额，计算 已 下一期的余额值
        if (isaNewBalance(one)) {
            nextBalance = debit.subtract(credit);
        }else {
            nextBalance = one.getBalance().add(debit).subtract(credit);

            //根据前一天余额值来更新 报表的 期初借贷方
            if(isCreditBalance(one.getBalance())){
                record.setBeginCredit(one.getBalance());
            }else{
                record.setBeginDebtor(one.getBalance());
            }
        }

        //期中发生额
        record.setInterimDebit(debit);
        record.setInterimCredit(credit);

        //期末借贷余额
        if (isCreditBalance(nextBalance)){
            record.setEndCredit(nextBalance.negate());
        }else{
            record.setEndDebtor(nextBalance);
        }
        //插入记录
        recordMapper.insert(record);
        return nextBalance;
    }

    private LedgerRecord getLedgerRecordByLastBalance(Calendar systemTime, LedgerBalance one) {
        LedgerRecord record = prototypeOfLedgerRecord;
        record.setSubjectSerial(one.getSubjectSerial());
        record.setBranchId(one.getBranch());
        record.setAccountTime(systemTime.getTime());

        return record;
    }

    private void setZeroAmountOfBeginAndEnd(LedgerRecord record) {
        //初始期初余额为 0
        record.setBeginDebtor(LedgerFlowHandle.ZERO_PROTOTYPE);
        record.setBeginCredit(LedgerFlowHandle.ZERO_PROTOTYPE);
        //初始期末余额为 0
        record.setEndCredit(LedgerFlowHandle.ZERO_PROTOTYPE);
        record.setEndDebtor(LedgerFlowHandle.ZERO_PROTOTYPE);
    }


    private boolean isCreditBalance(BigDecimal balance) {
        return balance.compareTo(LedgerFlowHandle.ZERO_PROTOTYPE) == -1;
    }


}

package com.dcits.internal.flowHandle;

import com.dcits.project.pojo.Subject;

import java.util.Date;


/**
 * 存款交易的流水记录
 *      额外的必须字段是
 *             交易发生的网点号。
 * @version: 1.0
**/
class DepositFlowHandle extends BaseFlowHandle{

    /**
     * 检查 网点号是否以提供
     * @how:
     * @return:
     *       void
    **/
    @Override
    protected void validateSpecialProperty() {
        if(transactionFlow.getBranchId()==null)
            throw new FlowHandleException("branch id is null!");
    }

    /**
     * 填充交易 对象、时间.
     * v2.2 交易对象不保存。
     * @how:
     * @return:
     *       void
     **/
    @Override
    protected void filledSpecialProperty() {
        this.setTime(new Date());

    }

    @Override
    protected void logLedgerFlow() {
        //记录 客户活期存款 总账流水
        setAccountSerial(Subject.CLIENT_DEMAND_DEPOSIT_SUBJECT)
                .setLoadRelationship(Subject.CREDIT)
                .insertLedgerFlow();

        //记录 现金流水
        setAccountSerial(Subject.CASH_SUBJECT)
                .setLoadRelationship(Subject.DEBIT)
                .insertLedgerFlow();

    }
}

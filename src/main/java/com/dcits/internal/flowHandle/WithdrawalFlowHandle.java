package com.dcits.internal.flowHandle;

import com.dcits.project.pojo.Subject;

import java.util.Date;

/**
 * 该子类大致与存款相同，所以后期可能会将 存取款的子类提取一个父类出来。
 * @version: 1.0
**/
class WithdrawalFlowHandle extends BaseFlowHandle{

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
     * 填充交易 对象、时间
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
        //客户总账，记借。
        setAccountSerial(Subject.CLIENT_DEMAND_DEPOSIT_SUBJECT)
                .setLoadRelationship(Subject.DEBIT)
                .insertLedgerFlow();

        //现金账户，记贷
        setAccountSerial(Subject.CASH_SUBJECT)
                .setLoadRelationship(Subject.CREDIT)
                .insertLedgerFlow();
    }
}

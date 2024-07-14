package com.dcits.internal.flowHandle;

import com.dcits.project.pojo.Subject;

import java.util.Date;
/**
 * 结息的流水记录。
 *      该类无必须值,默认值如下：
 *          分户流水的时间。  date
 *          交易对象。 默认现金卡
 *          支行。     网上银行。
 *
 * @version: 1.0
**/
class InterestSettlementFlowHandle extends BaseFlowHandle {

    @Override
    protected void validateSpecialProperty() {

    }

    @Override
    protected void filledSpecialProperty() {
        setBranchId(BaseFlowHandle.getOnlineOutlet())
                .setTime(new Date());
    }

    @Override
    protected void logLedgerFlow() {
        //应付利息的 贷
        setAccountSerial(Subject.INTEREST_PAYABLE_SUBJECT)
                .setLoadRelationship(Subject.CREDIT)
                .insertLedgerFlow();
        //利息支出，记借
        setAccountSerial(Subject.INTEREST_EXPENSE_SUBJECT)
                .setLoadRelationship(Subject.DEBIT)
                .insertLedgerFlow();

        //支付利息
        //客户活期账户。记贷
        setAccountSerial(Subject.CLIENT_DEMAND_DEPOSIT_SUBJECT)
                .setLoadRelationship(Subject.CREDIT)
                .insertLedgerFlow();

        //应付利息，记借
        setAccountSerial(Subject.INTEREST_PAYABLE_SUBJECT)
                .setLoadRelationship(Subject.DEBIT)
                .insertLedgerFlow();
    }
}

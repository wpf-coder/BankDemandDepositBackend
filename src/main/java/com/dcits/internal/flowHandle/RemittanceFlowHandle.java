package com.dcits.internal.flowHandle;

import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.Subject;

import java.util.Date;

/**
 * 负责汇款单一过程的流水记录，一定要与收款同用。
 * 该流水额外必须提供的字段有
 *          交易对象的 银行卡号
 *
 * 该流水 默认字段有（当字段为空时）
 *          发生支行。  网上银行
 *          发生时间。  当前操作系统的时间。
 *
 * @version: 1.0
**/
class RemittanceFlowHandle extends BaseFlowHandle {

    @Override
    protected void logLedgerFlow() {
        //客户总账，记借。
        setAccountSerial(Subject.CLIENT_DEMAND_DEPOSIT_SUBJECT)
                .setLoadRelationship(Subject.DEBIT)
                .insertLedgerFlow();
    }

    @Override
    protected void filledSpecialProperty() {
        //填充部分 transactionFlow 的字段
        if(transactionFlow.getBranchId() == null){
            transactionFlow.setBranchId(BaseFlowHandle.getOnlineOutlet());
            ledgerFlow.setBranchId(BaseFlowHandle.getOnlineOutlet());
        }

        if(transactionFlow.getTime() == null)
            setTime(new Date());

    }



    @Override
    protected void validateSpecialProperty() {
        if(!Card.validateCardId(transactionFlow.getCounterpartyCardNumber()))
            throw new FlowHandleException("交易对象的银行卡号格式不正确或为空");
    }
}

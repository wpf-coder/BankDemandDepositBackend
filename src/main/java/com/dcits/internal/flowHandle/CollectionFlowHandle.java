package com.dcits.internal.flowHandle;

import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.Subject;

import java.util.Date;

/**
 * 收款交易的流水生成
 * @version: 1.0
**/
class CollectionFlowHandle extends BaseFlowHandle{
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

    @Override
    protected void logLedgerFlow() {
        //记录 客户活期存款 总账流水 记贷
        setAccountSerial(Subject.CLIENT_DEMAND_DEPOSIT_SUBJECT)
                .setLoadRelationship(Subject.CREDIT)
                .insertLedgerFlow();

    }
}

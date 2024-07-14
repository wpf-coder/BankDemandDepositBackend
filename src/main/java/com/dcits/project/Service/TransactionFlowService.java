package com.dcits.project.service;


import com.dcits.project.mapper.CardMapper;
import com.dcits.project.mapper.LedgerFlowMapper;
import com.dcits.project.mapper.LedgerRecordMapper;
import com.dcits.project.mapper.TransactionFlowMapper;
import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.LedgerFlow;
import com.dcits.project.pojo.TransactionFlow;
import com.dcits.project.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionFlowService {
    @Autowired
    private TransactionFlowMapper mapper;
    @Autowired
    CardMapper cardMapper;
    @Autowired
    LedgerFlowMapper ledgerFlowMapper;

    @Autowired
    SystemInformationService systemInformationService;



    public Date getSystemDate(){
        return systemInformationService.getSystemDate();
    }


    public boolean addOneTransactionFlow(TransactionFlow flow){
        if(flow!=null){
            //flow.setTransactionSerial(TransactionFlowService.getUniqueSerialOfTransaction());
            return mapper.insert(flow);
        }
        return false;
    }

    public boolean addOneLedgerFlow(LedgerFlow flow){
        if (flow == null) {
            return false;
        }
        return ledgerFlowMapper.insert(flow);
    }


     /**
      * 根据卡号和交易类型及时间范围交易记录查询   柜员和用户接口
      * @Param
      *     cardId：银行卡号
      *     type 查询类型1存款、2取款、4转账、8利息结算  0为全部
      *     endTime  结束时间
      *     startTime  开始时间
      * @return: 
      *         交易记录list
     **/
    @Transactional(rollbackFor = RuntimeException.class)
    public List<TransactionFlow> queryTransactionFlow(String cardId, int type, Timestamp startTime, Timestamp endTime){

        List<TransactionFlow> list=mapper.queryTransactionFlow(cardId,type,startTime,endTime);
        for(TransactionFlow tf:list){
            if(tf.getCounterpartyCardNumber()==null){
                tf.setCounterpartyCardNumber("本行");
                continue;
            }
            tf.setCounterpartyCardNumber(
                    tf.getCounterpartyCardNumber().substring(0,4) +"****"+tf.getCounterpartyCardNumber().substring(15));
        }
        return  list;
    }



}

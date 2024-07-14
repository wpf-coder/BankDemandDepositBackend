package com.dcits.project.timing;

import com.dcits.project.DemoApplication;
import com.dcits.project.mapper.TransactionFlowMapper;
import com.dcits.project.pojo.TransactionFlow;
import com.dcits.project.service.SystemInformationService;
import com.dcits.project.service.TransactionFlowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

@SpringBootTest(classes = DemoApplication.class)
class LedgerFlowHandleTest {
    @Autowired
    LedgerFlowHandle ledgerFlowHandle;


    boolean needGenerationData = true;
    @Autowired
    TransactionFlowMapper transactionFlowMapper;
    @Autowired
    TransactionFlowService transactionFlowService;
    @Autowired
    private SystemInformationService systemInformationService;


    String subjectCard = "8888880000000000001";
    String partCard = "8888880000000000001";
    String branchId = "YSU-BANK";
    int flowNumber = 10;


    void generationData(){
        //现存一笔非常有钱的。
        TransactionFlow flow = new TransactionFlow();
        flow.setBranchId(branchId);
        flow.setTime(new Timestamp(systemInformationService.getSystemDate().getTime()));
        flow.setSubjectCardNumber(subjectCard);
        flow.setCounterpartyCardNumber(partCard);
        //无视规则，插入一些非发数据，即余额均为0
        flow.setSubjectCardBalance(new BigDecimal(0));
        for(int i=0;i<flowNumber;i++){
            flow.setType((i%2)+1);
            flow.setAmount(new BigDecimal(new Random().nextInt(10000)));
            //这里已经费了
            //transactionFlowService.saveOneFlow(flow);
        }


    }


    @Test
    void startCountCashFlow() {
        //generationData();
        ledgerFlowHandle.updateBalanceOfLedger(new Calendar.Builder().
                setInstant(systemInformationService.getSystemDate())
                .build());
    }

    @Test
    void countOneBranchCashFlow() {
    }
}
package com.dcits.project.mapper;

import com.dcits.project.DemoApplication;
import com.dcits.project.pojo.LedgerBalance;
import com.dcits.project.service.SystemInformationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoApplication.class)
class LedgerRecordMapperTest {
    @Autowired
    private LedgerRecordMapper mapper;
    @Autowired
    private LedgerBalanceMapper ledgerBalanceMapper;
    @Autowired
    private SystemInformationService systemInformationService;

    @Test
    void ledgerCorrespondingFlow() {
        Calendar calendar = new Calendar.Builder().setDate(2021,9-1,13).build();
        Calendar yesterday = new Calendar.Builder().setInstant(calendar.getTime()).build();
        yesterday.add(Calendar.DAY_OF_MONTH,-1);
        System.out.println("mapper = " + mapper.ledgerCorrespondingFlow(yesterday.getTime(),calendar.getTime(),"1001",3));
    }

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal(-5);
        System.out.println("a = " + a.compareTo(new BigDecimal(0)));
    }

    @Test
    void dataTest(){
        for(LedgerBalance balance : ledgerBalanceMapper.getLedgerAndBranchCorrespondingLedgerFLow(systemInformationService.getSystemDate(),1)){
            System.out.println("balance = " + balance);
        }

    }
}
package com.dcits.project.service;

import com.dcits.project.DemoApplication;
import com.dcits.project.mapper.CardMapper;
import com.dcits.project.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoApplication.class)
class CardServiceTest {
    @Autowired
    CardService cardService;
    @Autowired
    CardMapper cardMapper;

    @Test
    void deposit() {
        cardService.deposit("8888880000000000001",new BigDecimal(1000),"YSU-BANK");
    }

    @Test
    void queryCardInformation() {
        cardService.queryCardInformation("8888880000000000001");
    }

    @Test
    void testQueryCardInformation() {
        User user = new User();
        user.setId("123456789012345678");
        cardService.queryCardInformation(user.getId(),0,user);
    }

    @Test
    void test(){
        System.out.println("cardMapper = " + cardMapper.selectAllCardLinkDemandInterest());
    }
}
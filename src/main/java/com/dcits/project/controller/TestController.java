package com.dcits.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller

public class TestController {


//    @Authority
    @RequestMapping("/test")
    @ResponseBody
    public void test(){

//        service.tellerLogin("root","123456");
    }


}

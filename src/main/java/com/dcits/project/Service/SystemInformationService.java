package com.dcits.project.service;

import com.dcits.project.mapper.InterestRateMapper;
import com.dcits.project.mapper.SystemInformationMapper;
import com.dcits.project.pojo.InterestRate;
import com.dcits.project.pojo.SystemInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class SystemInformationService {

    @Autowired
    private SystemInformationMapper mapper;
    @Autowired
    private InterestRateMapper interestRateMapper;

    public static final String SERIAL = "YSU";
    public static final String DEMAND_RATE_CODE = "DEMAND_RATE";

    private static SystemInformation systemInformation = null;

    /**
     * 初始系统时间。目前处理数据库异常导致无法获取系统时间的问题
     * @how:
     * @return:
     *       boolean
    **/
    private synchronized boolean initSystemInformation(){
        if(SystemInformationService.systemInformation == null){
            SystemInformationService.systemInformation = mapper.selectByPrimaryKey(SystemInformationService.SERIAL);
        }
        return true;
    }

    /**
     * 获得唯一的系统时间的副本.
     * @how:
     * @return:
     *       java.util.Date
    **/
    public Date getSystemDate(){
        if(SystemInformationService.systemInformation == null){
            this.initSystemInformation();
        }
        return (Date) SystemInformationService.systemInformation.getSystemTime().clone();
    }

    /**
     * 获取系统当前日历的副本
     * @how:
     * @return:
     *       java.util.Calendar
    **/
    public Calendar getSystemCalendar(){
        if(SystemInformationService.systemInformation == null){
            this.initSystemInformation();
        }
        return (Calendar) SystemInformationService.systemInformation.getCalendar().clone();
    }

    /**
     * 更新系统时间，更新值是下一天。注意：该方法一旦调用，之前的时间均失效。
     * @how:
     * @return:
     *       void
    **/
    public void updateSystemTime(){
        SystemInformationService.systemInformation.jumpNextDay();
        mapper.updateByPrimaryKey(SystemInformationService.systemInformation);
    }

    /******************************利率部分**********************************/
    public InterestRate getDemandRate(){
        return interestRateMapper.selectByCodeAndTime(SystemInformationService.DEMAND_RATE_CODE,
                SystemInformationService.systemInformation.getSystemTime());
    }
}

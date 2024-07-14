package com.dcits.project.common;

import com.alibaba.fastjson.JSONObject;
import netscape.javascript.JSObject;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

/**
 * 获取某一时间戳的前一天、后一天、当天的0点   格式：（2021-09-11 00:00:00.0）
 */
public class DateUtil {
    /**
     * 获取后一天的0点
     * @param timestamp 当前时间戳
     * @return
     */
        public  static Timestamp nextDay(Timestamp timestamp){
            Calendar calendar = Calendar. getInstance ();
            calendar.setTime(timestamp);
            calendar.set(Calendar. HOUR_OF_DAY , 0);
            calendar.set(Calendar. MINUTE , 0);
            calendar.set(Calendar. SECOND , 0);
            calendar.set(Calendar. MILLISECOND , 0);
            calendar.add(Calendar. DAY_OF_MONTH , 1);
            Date date = new Date();
            date = calendar.getTime();
            timestamp=new Timestamp(date.getTime());
            return timestamp;
        }

    /**
     * 获取当前的0点
     * @param timestamp 当前时间戳
     * @return
     */
    public  static Timestamp today(Timestamp timestamp){
        Calendar calendar = Calendar. getInstance ();
        calendar.setTime(timestamp);
        calendar.set(Calendar. HOUR_OF_DAY , 0);
        calendar.set(Calendar. MINUTE , 0);
        calendar.set(Calendar. SECOND , 0);
        calendar.set(Calendar. MILLISECOND , 0);
        calendar.add(Calendar. DAY_OF_MONTH , 0);
        Date date = new Date();
        date = calendar.getTime();
        timestamp=new Timestamp(date.getTime());
        return timestamp;
    }

    /**
     * 获取前一天的0点
     * @param timestamp 当前时间戳
     * @return
     */
    public  static Timestamp lastDay(Timestamp timestamp){
        Calendar calendar = Calendar. getInstance ();
        calendar.setTime(timestamp);
        calendar.set(Calendar. HOUR_OF_DAY , 0);
        calendar.set(Calendar. MINUTE , 0);
        calendar.set(Calendar. SECOND , 0);
        calendar.set(Calendar. MILLISECOND , 0);
        calendar.add(Calendar. DAY_OF_MONTH , -1);
        Date date = new Date();
        date = calendar.getTime();
        timestamp=new Timestamp(date.getTime());
        return timestamp;
    }

    public  static Timestamp lastNumDay(Timestamp timestamp,int num ){
        Calendar calendar = Calendar. getInstance ();
        calendar.setTime(timestamp);
        calendar.set(Calendar. HOUR_OF_DAY , 0);
        calendar.set(Calendar. MINUTE , 0);
        calendar.set(Calendar. SECOND , 0);
        calendar.set(Calendar. MILLISECOND , 0);
        calendar.add(Calendar. DAY_OF_MONTH , -num+1);
        Date date = new Date();
        date = calendar.getTime();
        timestamp=new Timestamp(date.getTime());
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * @param num
     * @return
     */
    public  static Timestamp lastNumMonth(Timestamp timestamp,int num ){
        Calendar calendar = Calendar. getInstance ();
        calendar.setTime(timestamp);
        calendar.set(Calendar. HOUR_OF_DAY , 0);
        calendar.set(Calendar. MINUTE , 0);
        calendar.set(Calendar. SECOND , 0);
        calendar.set(Calendar. MILLISECOND , 0);
        calendar.add(Calendar.MONTH,-num);
        Date date = new Date();
        date = calendar.getTime();
        timestamp=new Timestamp(date.getTime());
        return timestamp;
    }

    public static void main(String[] args) {
        System.out.println(lastNumMonth(new Timestamp(System.currentTimeMillis()),3));

    }
}

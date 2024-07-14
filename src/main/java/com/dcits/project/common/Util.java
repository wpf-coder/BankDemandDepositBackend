package com.dcits.project.common;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component
public class Util {
    /**
     * 使用md5的算法进行加密
     */
    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        StringBuilder md5code= new StringBuilder(new BigInteger(1, secretBytes).toString(16));// // 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.append('0');
        }
        System.out.println(md5code);
        return md5code.toString();
    }

    public static boolean verificationAmount(String amount){
        return amount!=null;
    }


    public static void print(Object...strings){
        System.out.print("\n");
        for(Object s : strings) {
            System.out.print(s+" ");
        }
        System.out.print("\n");
    }
    public static boolean isState(int state, int needState){
        return (state & needState) == state;
    }

    /**
     * 将状态进行或运算，只适合二进制位的状态值。
     * @how:
     * @return:
     *       int
    **/
    public static int mergeState(int state,int mergingState){
        return state | mergingState;
    }

    public static void main(String[] args) {

//        System.out.println("md5(\"123456\") = " + md5("123456"));
//        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        simpleDate.
//        System.out.println(Timestamp.valueOf(simpleDate.format(new Date())));
    }
}

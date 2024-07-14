package com.dcits.project.dto;

import lombok.*;

/**
 * UserEstablish: 接收前端开户数据
 * @version: 1.0
**/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEstablish {

    /** 不开通网银 */
    public static final int NON_OPEN_EBANK_STATE=0;
    /** 开通网银 */
    public static final int OPEN_EBANK_STATE=1;

    /*身份证*/
    private String id;
    private String name;
    private String phone;
    private String address;
    private String email;
    /*1未开通、2开通*/
    private int ebankState;
    /*1未开通、2开通*/
    private int mobileBankState;
    /*1未开通、2开通*/
    private int foreignExchangeState;
    //银行卡密码
    private String cardPwd;
    // 网银密码
    private String ebankPwd;


    public boolean valicate(){
        if(this.id==null||this.id.equals("")){
            return false;
        }
        if(this.name==null||this.name.equals("")){
            return false;
        }
        if(this.phone==null||this.phone.equals("")){
            return false;
        }
        if(this.address==null||this.address.equals("")){
            return false;
        }
        if(this.cardPwd==null||this.cardPwd.equals("")){
            return false;
        }
        return true;
    }
}

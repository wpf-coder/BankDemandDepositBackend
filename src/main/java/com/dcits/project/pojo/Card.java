package com.dcits.project.pojo;

import java.sql.*;

import com.alibaba.fastjson.annotation.JSONField;
import com.sun.istack.internal.NotNull;
import lombok.*;
import java.math.BigDecimal;
/**
 * table name:  card
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card{

	/** 银行卡正常 */
	public static final int NORMAL_CARD_STATE 	= 		1;
	/** 银行卡久悬 */
	public static final int LONG_HANG_CARD_STATE	=	2;


	private String id;
	private String userId;
	private String password;
	private BigDecimal balance;
	/*1 是正常，2是久悬*/
	private Integer state;

	//仅在联合查询时有效，不纳入序列化当中。
	private transient DemandInterest demandInterest;

	//暂时不维护该字段，久悬户的识别由 定时任务识别。
	private Timestamp lastTime;


	public static boolean validateCardId(String cardId){
		return cardId!=null && cardId.matches("^[0-9]{19,22}$");
	}

	public static void main(String[] args) {
		System.out.println("validateCardId(\"8888881631584014910\") = " + validateCardId("8888881631584014910"));
	}

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"id\":\"")
				.append(id).append('\"')
				.append(",\"userId\":\"")
				.append(userId).append('\"')
				.append(",\"password\":\"")
				.append(password).append('\"')
				.append(",\"balance\":\"")
				.append(balance).append('\"')
				.append(",\"state\":\"")
				.append(state).append('\"')
				.append(",\"lastTime\":\"")
				.append(lastTime).append('\"')
				.append("}")
				.toString();
	}
}


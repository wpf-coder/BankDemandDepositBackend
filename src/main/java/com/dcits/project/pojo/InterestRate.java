package com.dcits.project.pojo;

import java.util.Date;
import lombok.*;
import java.math.BigDecimal;
/**
 * table name:  interest_rate
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterestRate{
	// 数据库初始数据中，活期利率的代号。
	public static final String DEMAND_INTEREST_RATE = "DEMAND_RATE";

	private Integer serial;
	private String code;
	private Date effectiveTime;
	private Date expirationTime;
	/*C 是活期，其他暂时不处理*/
	private String type;
	/*利率的基数，默认360天*/
	private String baseTime;
	/*利率值*/
	private BigDecimal value;
	private Date updateTime;
	/*上一次更新的人，默认admitNO:1*/
	private String updateOperator;

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"serial\":\"")
				.append(serial).append('\"')
				.append(",\"code\":\"")
				.append(code).append('\"')
				.append(",\"effectiveTime\":\"")
				.append(effectiveTime).append('\"')
				.append(",\"expirationTime\":\"")
				.append(expirationTime).append('\"')
				.append(",\"type\":\"")
				.append(type).append('\"')
				.append(",\"baseTime\":\"")
				.append(baseTime).append('\"')
				.append(",\"value\":\"")
				.append(value).append('\"')
				.append(",\"updateTime\":\"")
				.append(updateTime).append('\"')
				.append(",\"updateOperator\":\"")
				.append(updateOperator).append('\"')
				.append("}")
				.toString();
	}
}


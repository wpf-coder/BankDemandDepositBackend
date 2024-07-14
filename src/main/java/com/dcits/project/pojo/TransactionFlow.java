package com.dcits.project.pojo;

import java.sql.*;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import java.math.BigDecimal;
/**
 * table name:  transaction_flow
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFlow{


	/*自增ID，流水记录编号*/
	private Integer serial;
	//发生时间
	@JSONField(format = "YYYY-MM-DD-hh-mm-ss")
	private Timestamp time;
	//交易发生的网点号
	private String branchId;
	//交易的编号
	private String transactionSerial;

	/*该记录主体的银行卡*/
	private String subjectCardNumber;
	/*发生金额	*/
	private BigDecimal amount;
	/*主体交易后的余额	*/
	private BigDecimal subjectCardBalance;

	//由 BaseFlowHandle 定义该字段的魔数意义。
	private Integer type;


	/*交易对象的银行（卡）账户*/
	private String counterpartyCardNumber;

	private Date accountDate;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("\"serial\":")
				.append(serial);
		sb.append(",\"time\":\"")
				.append(time).append('\"');
		sb.append(",\"branchId\":\"")
				.append(branchId).append('\"');
		sb.append(",\"transactionSerial\":\"")
				.append(transactionSerial).append('\"');
		sb.append(",\"subjectCardNumber\":\"")
				.append(subjectCardNumber).append('\"');
		sb.append(",\"amount\":")
				.append(amount);
		sb.append(",\"subjectCardBalance\":")
				.append(subjectCardBalance);
		sb.append(",\"type\":")
				.append(type);
		sb.append(",\"counterpartyCardNumber\":\"")
				.append(counterpartyCardNumber).append('\"');
		sb.append(",\"accountDate\":\"")
				.append(accountDate).append('\"');
		sb.append('}');
		return sb.toString();
	}
}


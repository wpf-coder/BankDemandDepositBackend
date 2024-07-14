package com.dcits.project.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * table name:  ledger_flow
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerFlow{

	private Integer serial;
	private String subjectSerial;
	private Integer loadRelationship;
	private String transactionSerial;
	private BigDecimal amount;
	private String branchId;
	private Date date;

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"serial\":\"")
				.append(serial).append('\"')
				.append(",\"subjectSerial\":\"")
				.append(subjectSerial).append('\"')
				.append(",\"loadRelationship\":\"")
				.append(loadRelationship).append('\"')
				.append(",\"transactionSerial\":\"")
				.append(transactionSerial).append('\"')
				.append(",\"amount\":\"")
				.append(amount).append('\"')
				.append(",\"branchId\":\"")
				.append(branchId).append('\"')
				.append(",\"date\":\"")
				.append(date).append('\"')
				.append("}")
				.toString();
	}


}


package com.dcits.project.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * table name:  ledger_balance
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerBalance{
	public static final int OPEN_STATE = 1;

	private String subjectSerial;
	private BigDecimal balance;
	private String branch;
	/*1 是正常，2是关闭*/
	private Integer state;
	private String account;
	private String accountDescription;

	private transient List<LedgerFlow> flows;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("\"subjectSerial\":\"")
				.append(subjectSerial).append('\"');
		sb.append(",\"balance\":")
				.append(balance);
		sb.append(",\"branch\":\"")
				.append(branch).append('\"');
		sb.append(",\"state\":")
				.append(state);
		sb.append(",\"account\":\"")
				.append(account).append('\"');
		sb.append(",\"accountDescription\":\"")
				.append(accountDescription).append('\"');
		sb.append(",\"flows\":")
				.append(flows);
		sb.append('}');
		return sb.toString();
	}
}


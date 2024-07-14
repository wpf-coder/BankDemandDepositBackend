package com.dcits.project.pojo;

import lombok.*;
/**
 * table name:  bank_card_serial
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankCardSerial{
	/** 银行卡号默认前缀 */
	public static final String SELF_BANK_PREFIX = "888888";

	/** 对应银行卡前缀 */
	private String prefix;
	/** 发卡行的代号 */
	private String issueBank;
	/** 本卡与本行的关系,I是本行卡的开头，E是外行 */
	private String relationship;

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"prefix\":\"")
				.append(prefix).append('\"')
				.append(",\"issueBank\":\"")
				.append(issueBank).append('\"')
				.append(",\"relationship\":\"")
				.append(relationship).append('\"')
				.append("}")
				.toString();
	}
}


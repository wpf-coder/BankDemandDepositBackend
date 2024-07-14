package com.dcits.project.pojo;

import java.util.Date;
import lombok.*;
import java.math.BigDecimal;
/**
 * table name:  settlement_interest_log
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInterestLog{

	private Integer serial;
	/*此次结息的时间*/
	private Date time;
	private String cardId;
	private BigDecimal interest;
	private BigDecimal cumulativeInterest;
	private Date lastTime;

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"serial\":\"")
				.append(serial).append('\"')
				.append(",\"time\":\"")
				.append(time).append('\"')
				.append(",\"cardId\":\"")
				.append(cardId).append('\"')
				.append(",\"interest\":\"")
				.append(interest).append('\"')
				.append(",\"cumulativeInterest\":\"")
				.append(cumulativeInterest).append('\"')
				.append(",\"lastTime\":\"")
				.append(lastTime).append('\"')
				.append("}")
				.toString();
	}
}


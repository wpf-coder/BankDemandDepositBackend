package com.dcits.project.pojo;

import java.util.Date;
import lombok.*;
import java.math.BigDecimal;
/**
 * table name:  demand_interest
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandInterest{

	private String cardId;
	private BigDecimal cumulativeInterest;
	private Date lastCountTime;
	private Date lastSettleTime;

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"cardId\":\"")
				.append(cardId).append('\"')
				.append(",\"cumulativeInterest\":\"")
				.append(cumulativeInterest).append('\"')
				.append(",\"lastCountTime\":\"")
				.append(lastCountTime).append('\"')
				.append(",\"lastSettleTime\":\"")
				.append(lastSettleTime).append('\"')
				.append("}")
				.toString();
	}
}


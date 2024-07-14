package com.dcits.project.pojo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * table name:  cash_ledger_record
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerRecord {

	private Integer serial;
	private String branchId;


	private String subjectSerial;

	private Date accountTime;
	private BigDecimal beginDebtor;
	private BigDecimal beginCredit;
	private BigDecimal interimDebit;
	private BigDecimal interimCredit;
	private BigDecimal endDebtor;
	private BigDecimal endCredit;

	private transient List<TransactionFlow> flows;


	public static LedgerRecord getNextRecord(LedgerRecord ledgerRecord,Date date){
		LedgerRecord record = new LedgerRecord();
		//起初等于期末
		record.setBeginCredit(ledgerRecord.getEndCredit());
		record.setBeginDebtor(ledgerRecord.getEndDebtor());
		//科目 网点号一致
		record.setBranchId(ledgerRecord.getBranchId());
		record.setSubjectSerial(ledgerRecord.getSubjectSerial());
		//时间为下一天
		record.setAccountTime(date);


		return record;
	}

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"serial\":\"")
				.append(serial).append('\"')
				.append(",\"branchId\":\"")
				.append(branchId).append('\"')
				.append(",\"accountTime\":\"")
				.append(accountTime).append('\"')
				.append(",\"beginDebtor\":\"")
				.append(beginDebtor).append('\"')
				.append(",\"beginCredit\":\"")
				.append(beginCredit).append('\"')
				.append(",\"interimDebit\":\"")
				.append(interimDebit).append('\"')
				.append(",\"interimCredit\":\"")
				.append(interimCredit).append('\"')
				.append(",\"endDebtor\":\"")
				.append(endDebtor).append('\"')
				.append(",\"endCredit\":\"")
				.append(endCredit).append('\"')
				.append("}")
				.toString();
	}
}


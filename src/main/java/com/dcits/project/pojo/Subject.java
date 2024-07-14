package com.dcits.project.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * table name:  subject
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject{
	//科目编号字段。
	public static final String CASH_SUBJECT = "1001";							//现金科目
	public static final String INTEREST_PAYABLE_SUBJECT = "260001";				//应付利息科目
	public static final String INTEREST_EXPENSE_SUBJECT = "640002";				//利息支出科目
	public static final String CLIENT_DEMAND_DEPOSIT_SUBJECT = "215001";		//用户活期存款科目

	//借贷性质字段
	public static final int CREDIT = 1;			//贷
	public static final int DEBIT  = 0;			//借

	private String serial;
	private String name;
	/*0是借、1是贷*/
	private Integer loadRelationship;

	public static boolean validateSubjectSerial(String serial){
		return serial!=null && serial.matches("^[0-9]{4,6}$");
	}

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"serial\":\"")
				.append(getSerial()).append('\"')
				.append(",\"name\":\"")
				.append(name).append('\"')
				.append(",\"loadRelationship\":\"")
				.append(loadRelationship).append('\"')
				.append("}")
				.toString();
	}
}


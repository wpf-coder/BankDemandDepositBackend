package com.dcits.project.pojo;

import lombok.*;
import sun.security.provider.PolicySpiFile;

/**
 * table name:  user
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User{
	/** 网银  未开通 */
	public static final int NON_OPEN_EBANK_STATE=1;
	/** 网银 已开通 */
	public static final int OPEN_EBANK_STATE=2;
	/** 手机银行  未开通 */
	public static final int NON_OPEN_MOBILE_BANK_STATE=1;
	/** 手机银行  已开通 */
	public static final int OPEN_MOBILE_BANK_STATE=2;
	/** 外汇  未开通 */
	public static final int NON_OPEN_FOREIGN_EXCHANGE_STATE=1;
	/** 外汇  已开通 */
	public static final int OPEN_FOREIGN_EXCHANGE_STATE=2;


	/*身份证*/
	private String id;
	private String name;
	private String phone;
	private String address;

	private String email;
	/*1未开通、2开通*/
	private Integer ebankState;
	/*1未开通、2开通*/
	private Integer mobileBankState;
	/*1未开通、2开通*/
	private Integer foreignExchangeState;

	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof User){
			User y = (User) obj;
			return this.getId()!=null && getId().equals(y.getId());
		}
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"id\":\"")
				.append(id).append('\"')
				.append(",\"name\":\"")
				.append(name).append('\"')
				.append(",\"phone\":\"")
				.append(phone).append('\"')
				.append(",\"address\":\"")
				.append(address).append('\"')
				.append(",\"email\":\"")
				.append(email).append('\"')
				.append(",\"ebankState\":\"")
				.append(ebankState).append('\"')
				.append(",\"mobileBankState\":\"")
				.append(mobileBankState).append('\"')
				.append(",\"foreignExchangeState\":\"")
				.append(foreignExchangeState).append('\"')
				.append("}")
				.toString();
	}
}


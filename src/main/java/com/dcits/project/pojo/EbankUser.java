package com.dcits.project.pojo;

import lombok.*;
/**
 * table name:  ebank_user
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EbankUser{
	/** 已登录 */
	public static final int INLINE_STATE=1;
	/** 未登录 */
	public static final int OFFLINE_STATE=0;

	private String id;
	private String password;
	private Integer state;

	public static String getInitPassword(User user){
		return  "@"+user.getId().substring(user.getId().length()-6)
				+"a"+user.getPhone().substring(user.getPhone().length()-6);
	}


	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"id\":\"")
				.append(id).append('\"')
				.append(",\"password\":\"")
				.append(password).append('\"')
				.append(",\"state\":\"")
				.append(state).append('\"')
				.append("}")
				.toString();
	}
}


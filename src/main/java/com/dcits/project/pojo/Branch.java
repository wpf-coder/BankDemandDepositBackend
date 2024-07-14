package com.dcits.project.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
/**
 * table name:  branch
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Branch{
	public static final String ONLINE_ID = "NETWORK-BANK";


	private String id;
	private String name;
	private String address;
	private String phone;

	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"id\":\"")
				.append(id).append('\"')
				.append(",\"name\":\"")
				.append(name).append('\"')
				.append(",\"address\":\"")
				.append(address).append('\"')
				.append(",\"phone\":\"")
				.append(phone).append('\"')
				.append("}")
				.toString();
	}
}


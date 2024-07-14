package com.dcits.project.pojo;

import lombok.*;
/**
 * table name:  teller
 */ 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teller{

	private String id;
	private String password;
	private String branchId;
	
	@Override
	public String toString() {
		return new StringBuilder("{")
				.append("\"id\":\"")
				.append(id).append('\"')
				.append(",\"password\":\"")
				.append(password).append('\"')
				.append(",\"branchId\":\"")
				.append(branchId).append('\"')
				.append("}")
				.toString();
	}
}


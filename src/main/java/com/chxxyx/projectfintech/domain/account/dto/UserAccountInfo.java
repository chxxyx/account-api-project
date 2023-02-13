package com.chxxyx.projectfintech.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountInfo {

	private String username;
	private String name;
	private String accountNumber;
	private String accountPassword;
}

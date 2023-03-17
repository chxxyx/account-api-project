package com.chxxyx.projectfintech.domain.user.model;

import com.chxxyx.projectfintech.domain.user.type.UserRole;
import java.time.LocalDateTime;
import java.util.List;
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
public class UserInfo {

	private String SSN;
	private String name;//유저 이름
	private String username; //유저 id
	private String password;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private UserRole role; // 관리자, 회원 구분

}

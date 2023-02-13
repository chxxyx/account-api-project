package com.chxxyx.projectfintech.domain.user.dto;

import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

	private String ssn;//주민번호
	private String name;//유저 이름
	private String username; //유저 id
	private String password;

	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private UserRole role; // 관리자, 회원 구분

	public static UserDto fromEntity(User user) {
		return UserDto.builder().ssn(user.getSSN()).name(user.getName())
			.username(user.getUsername()).password(user.getPassword())
			.createdAt(user.getCreatedAt()).modifiedAt(user.getModifiedAt()).role(user.getRole())
			.build();
	}

}

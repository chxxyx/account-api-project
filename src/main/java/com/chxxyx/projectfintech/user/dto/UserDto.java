package com.chxxyx.projectfintech.user.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {

	@NotEmpty(message = "사용자 주민 번호는 필수 항목입니다.")
	private String SSN;//주민번호

	@NotEmpty(message = "사용자 이름은 필수 항목입니다.")
	private String userName;//유저 이름

	@NotEmpty(message = "사용자 비밀번호는 필수 항목입니다.")
	private String password;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

	@NotEmpty(message = "사용자 ID(email)는 필수 항목입니다.")
	@Email
	private String emailId; //유저 id
	private Boolean emailAuthYn; //이메일 인증 여부
	private String userType; // 관리자, 회원 구분


}

package com.chxxyx.projectfintech.user.dto;

import com.chxxyx.projectfintech.user.type.UserRole;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {

	@NotBlank(message = "사용자 주민 번호는 필수 항목입니다.")
	private String SSN;//주민번호

	@NotBlank(message = "사용자 이름은 필수 항목입니다.")
	private String name;//유저 이름

	@NotBlank(message = "사용자 비밀번호는 필수 항목입니다.")
	private String password;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

	@NotBlank(message = "사용자 ID(email)는 필수 항목입니다.")
	@Email
	private String username; //유저 id
	private UserRole role; // 관리자, 회원 구분


}

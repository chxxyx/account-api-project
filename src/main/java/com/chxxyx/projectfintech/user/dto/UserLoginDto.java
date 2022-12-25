package com.chxxyx.projectfintech.user.dto;

import com.chxxyx.projectfintech.user.type.UserRole;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoginDto {

	@NotBlank(message = "사용자 ID는 필수 항목입니다.")
	@Pattern(regexp = "[a-zA-Z0-9]{4,9}",
		message = "아이디는 영문, 숫자만 가능하며 4 ~ 10자리 이내로 입력해주세요.")
	private String username; //유저 id

	@NotBlank(message = "사용자 비밀번호는 필수 항목입니다.")
	@Pattern(regexp="[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6 ~ 12자리 이내로 입력해주세요.")
	private String password;

	private UserRole role; // 관리자, 회원 구분

}

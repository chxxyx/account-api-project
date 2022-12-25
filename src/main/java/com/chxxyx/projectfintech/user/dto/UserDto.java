package com.chxxyx.projectfintech.user.dto;

import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.type.UserRole;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

	@NotBlank(message = "사용자 주민 번호는 필수 항목입니다.")
//	@Pattern(regexp = "^(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))-[1-4][0-9]{6}$"
//			, message = "주민번호는 숫자 13자리를 입력해주세요.")
	private String SSN;//주민번호

	@NotBlank(message = "사용자 이름은 필수 항목입니다.")
	@Size(min = 2, max = 8, message = "이름을 2~8자 사이로 입력해주세요.")
	private String name;//유저 이름

	@NotBlank(message = "사용자 ID는 필수 항목입니다.")
	@Pattern(regexp = "[a-zA-Z0-9]{4,9}", message = "아이디는 영문, 숫자만 가능하며 4 ~ 10자리 이내로 입력해주세요.")
	private String username; //유저 id

	@NotBlank(message = "사용자 비밀번호는 필수 항목입니다.")
	@Pattern(regexp = "[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6 ~ 12자리 이내로 입력해주세요.")
	private String password;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;
	private UserRole role; // 관리자, 회원 구분

	public static UserDto fromEntity(User user) {
		return UserDto.builder().SSN(user.getSSN()).name(user.getName())
			.username(user.getUsername()).password(user.getPassword())
			.createdAt(user.getCreatedAt()).modifiedAt(user.getModifiedAt()).role(user.getRole())
			.build();
	}

}

package com.chxxyx.projectfintech.domain.admin.model;

import com.chxxyx.projectfintech.domain.user.model.UserDto;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ChangeUserType {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {

		@NotBlank
		private String username;
		@NotNull
		private UserRole role;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response { //이너 클래스

		private String username;
		private UserRole role;

		public static ChangeUserType.Response from(UserDto userDto) {

			return Response.builder().username(userDto.getUsername()).role(userDto.getRole())
				.build();
		}
	}
}

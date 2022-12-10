package com.chxxyx.projectfintech.user.controller;

import com.chxxyx.projectfintech.config.TokenProvider;
import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.entity.User;
import com.chxxyx.projectfintech.user.service.UserService;
import com.chxxyx.projectfintech.user.type.UserRole;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {
	private final UserService userService;
	private final TokenProvider tokenProvider;

	// 로그인
	@PostMapping( "/user/login")
	public String login(@RequestBody UserDto userDto, HttpServletResponse response) {

		log.debug(userDto.getUsername());
		User user = userService.login(userDto);
		String username = user.getUsername();
		UserRole role = user.getRole();
		String token = tokenProvider.generatedToken(username, role);
		log.info("user role : "+user.getRole());
		response.setHeader("JWT", token);

		return token;

	}

}

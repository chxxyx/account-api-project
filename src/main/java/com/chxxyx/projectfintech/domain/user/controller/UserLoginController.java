package com.chxxyx.projectfintech.domain.user.controller;

import com.chxxyx.projectfintech.domain.user.dto.LoginUser;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.service.UserService;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import com.chxxyx.projectfintech.config.jwt.TokenProvider;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {

	private final UserService userService;
	private final TokenProvider tokenProvider;

	// 로그인
	@PostMapping("/user/login")
	public String login(@RequestBody @Valid LoginUser userLoginDto,
		HttpServletResponse response) {

		log.debug(userLoginDto.getUsername());
		User user = userService.login(userLoginDto);
		String username = user.getUsername();
		UserRole role = user.getRole();
		String token = tokenProvider.generatedToken(username, role);
		log.info("user role : " + user.getRole());
		response.setHeader("JWT", token);

		return token;

	}

}

package com.chxxyx.projectfintech.domain.user.controller;

import com.chxxyx.projectfintech.config.jwt.JwtAuthenticationFilter;
import com.chxxyx.projectfintech.domain.user.model.LoginUser;
import com.chxxyx.projectfintech.domain.user.entity.User;
import com.chxxyx.projectfintech.domain.user.model.RegisterUser;
import com.chxxyx.projectfintech.domain.user.model.TokenDto;
import com.chxxyx.projectfintech.domain.user.service.UserService;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import com.chxxyx.projectfintech.config.jwt.TokenProvider;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final TokenProvider tokenProvider;

	@PostMapping("/register")
	public RegisterUser.Response userRegister(@RequestBody @Valid RegisterUser.Request request) {

		log.info("::::::::::" + request);

		return RegisterUser.Response.from(userService.registerUser(request));

	}

	/*
	 	로그인
	 */
	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginUser loginUser) {

		String token = userService.login(loginUser);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtAuthenticationFilter.TOKEN_HEADER,
			JwtAuthenticationFilter.TOKEN_PREFIX + token);

		return new ResponseEntity<>(new TokenDto(token), httpHeaders, HttpStatus.OK);

	}

}

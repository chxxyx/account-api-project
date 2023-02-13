package com.chxxyx.projectfintech.domain.user.controller;

import com.chxxyx.projectfintech.domain.user.dto.RegisterUser;
import com.chxxyx.projectfintech.domain.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserRegisterController {

	private final UserService userService;

	@PostMapping("/user/register")
	public RegisterUser.Response userRegister(@RequestBody @Valid RegisterUser.Request request) {

		log.info("::::::::::" + request);

		return RegisterUser.Response.from(
			userService.registerUser(request.getUsername(), request.getPassword(),
				request.getName(), request.getSsn()));

	}

}

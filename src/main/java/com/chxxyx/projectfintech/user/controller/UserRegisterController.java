package com.chxxyx.projectfintech.user.controller;

import com.chxxyx.projectfintech.user.dto.RegisterUser;
import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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

package com.chxxyx.projectfintech.user.controller;

import com.chxxyx.projectfintech.user.dto.UserDto;
import com.chxxyx.projectfintech.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class UserController {



	private final UserService userService;


	@GetMapping("/user/register")
	public String userRegister(UserDto userDto) {

		return "user/register";
	}

	@PostMapping("/user/register")
	public String userRegisterSubmit(
		@Valid UserDto parameter, BindingResult bindingResult, Model model) {
		model.addAttribute("userDto", parameter);

		if (bindingResult.hasErrors()) {
			return "user/register";
		}

		boolean result = userService.userRegister(parameter);
		model.addAttribute("result", result);


		return "redirect:/";
	}
	// 로그인
	@RequestMapping( "/user/login")
	public String login() {

		return "user/login";

	}

}

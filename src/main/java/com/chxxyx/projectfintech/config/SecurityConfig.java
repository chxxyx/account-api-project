package com.chxxyx.projectfintech.config;

import com.chxxyx.projectfintech.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserService userService;
	AuthenticationManager authenticationManager;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userService);
		authenticationManager = authenticationManagerBuilder.build();

		// (rest api) 외부에서의 호출을 위한
		http
			//.cors().disable()
			.csrf().disable(); // csrf 토큰 비활성화
		http    .headers().frameOptions().sameOrigin();

		http.authorizeRequests()
			.antMatchers("/",
				"/user/register").permitAll() /// permitAll(모든 접근 가능 )

			// 관리자
			.antMatchers("/admin/**")
			.hasRole("ADMIN")

			.anyRequest()
			.authenticated()
			.and()
			.authenticationManager(authenticationManager)
			.formLogin()
			.loginPage("/user/login")
			.defaultSuccessUrl("/")
			.failureHandler(getFailureHandler()).permitAll() // 실패 요청 처리 핸들러
			.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) // logout url
			.logoutSuccessUrl("/") // 성공시 리턴 url
			.invalidateHttpSession(true) // 인증정보 지우고 세션 무효화
			.and()
			.exceptionHandling()
		;

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserAuthenticationFailureHandler getFailureHandler() {
		return new UserAuthenticationFailureHandler();
	}

}
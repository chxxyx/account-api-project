package com.chxxyx.projectfintech.config.security;

import com.chxxyx.projectfintech.config.jwt.JwtAuthenticationFilter;
import com.chxxyx.projectfintech.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final UserService userService;
	private final JwtAuthenticationFilter authenticationFilter;
	AuthenticationManager authenticationManager;

	@Bean
	public WebSecurityCustomizer configure() {

		return (web) -> web.ignoring().antMatchers("/ignore1");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors().disable().csrf().disable(); // csrf 토큰 비활성화

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests().antMatchers("/", "/user/register", "/user/login")
			.permitAll() /// permitAll(모든 접근 가능 )

			// 관리자
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/user/**", "/account/**", "/transaction/**").hasRole("USER").anyRequest()
			.authenticated();

		//JwtFilter 추가
		http.addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
package com.chxxyx.projectfintech.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		String message = "로그인에 실패하였습니다.";
		if (exception instanceof InternalAuthenticationServiceException){

			message = exception.getMessage();
		}

		setUseForward(true);
		setDefaultFailureUrl("/user/login?error=true");
		request.setAttribute("errorMessage", message);

		super.onAuthenticationFailure(request, response, exception);
	}
}

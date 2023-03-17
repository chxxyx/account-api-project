package com.chxxyx.projectfintech.config.jwt;

import com.chxxyx.projectfintech.domain.user.model.UserDto;
import com.chxxyx.projectfintech.domain.user.service.AuthorityService;
import com.chxxyx.projectfintech.domain.user.service.UserService;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {
	private final AuthorityService authorityService;
	@Value("${jwt.token.key}")
	private String secretKey;
	//토큰 유효시간 설정
	private Long tokenValidTime = 240 * 60 * 1000L;

	//secretkey를 미리 인코딩 해줌.
	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String generatedToken(String username, UserRole role) {

		Date now = new Date();
		//사용자 권한 저장
		Claims claims = Jwts.claims().setSubject("access_token") // 토큰 제목
			.setIssuedAt(now) // 발행시간
			.setExpiration(new Date(now.getTime() + tokenValidTime)); // 토큰 만료 기한

		// private claims
		claims.put("username", username); // 정보는 key - value 쌍으로 저장
		claims.put("role", role);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT") //헤더
			.setClaims(claims) // 페이로드
			.signWith(SignatureAlgorithm.HS512, secretKey)  // 서명. 사용할 암호화 알고리즘과 signature 에 들어갈 secretKey 세팅
			.compact();
	}

	// JWT 토큰에서 인증 정보 조회
//	public Authentication getAuthentication(String token) {
//		UserDetails userDetails = authorityService.loadUserByUsername(this.getUserPk(token));
//		return new UsernamePasswordAuthenticationToken(userDetails, "",
//			userDetails.getAuthorities());
//	}

	// 토큰에서 회원 정보 추출
	public String getUserPk(String token) {
		return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
			.get("username");
	}

	// Request의 Header에서 token 값을 가져 옴
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("JWT");
	}

	// 토큰의 유효성 + 만료일자 확인  // -> 토큰이 expire되지 않았는지 True/False로 반환해줌.
	public boolean validateToken(String jwtToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
				.getBody();

			return !claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

}

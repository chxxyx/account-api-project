package com.chxxyx.projectfintech.config.jwt;

import com.chxxyx.projectfintech.domain.user.service.UserService;
import com.chxxyx.projectfintech.domain.user.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {
	private final UserService userService;
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
		Claims claims = Jwts.claims()
				.setSubject(username)
				.setIssuedAt(now) //발행시간
				.setExpiration(new Date(now.getTime() + tokenValidTime)); // 토큰 만료기한)
		claims.put("role", role);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT") //헤더
			.setClaims(claims) // 페이로드
			.signWith(SignatureAlgorithm.HS512, secretKey)  // 서명. 사용할 암호화 알고리즘과 signature 에 들어갈 secretKey 세팅
			.compact();
	}

	public UsernamePasswordAuthenticationToken getAuthentication(String jwt) {
		UserDetails userDetails =this.userService.loadUserByUsername(this.getUsername(jwt));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return this.parseClaims(token).getSubject();
	}
	public boolean validateToken(String token) {
		if (!StringUtils.hasText(token)) {
			return false;
		}
		var claims = this.parseClaims(token);
		return !claims.getExpiration().before(new Date());
	}
	private Claims parseClaims(String token){
		try {
			return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}


}

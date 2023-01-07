package com.chxxyx.projectfintech;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.chxxyx.projectfintech.config.JasyptConfig;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class EncryptionApplicationTest extends JasyptConfig {

	@Test
	public void jasypt_암호화_복호화() {
	    // given
		String mariaDB_URL = "jdbc:mariadb://localhost:3306/project_2";
		String mariaDB_userName = "root";
		String mariaDB_password = "1234";
		String jwtTokenKey = "aGVsbG8K";

	    // when
		String encryptedURL = jasyptEncrypt(mariaDB_URL);
		System.out.println("DB URL 암호화 된 값 :::: " + encryptedURL);

		String encrypteduserName = jasyptEncrypt(mariaDB_userName);
		System.out.println("DB userName 암호화 된 값 :::: " + encrypteduserName);

		String encryptedPw = jasyptEncrypt(mariaDB_password);
		System.out.println("DB userPW 암호화 된 값 :::: " + encryptedPw);

		String encryptedJwtTokenKey = jasyptEncrypt(jwtTokenKey);
		System.out.println("jwt token key 암호화 된 값 :::: " + encryptedJwtTokenKey);

		System.out.println("url 복호화 :::::::: " + jasyptDecrypt(encryptedURL));
		System.out.println("name 복호화 :::::::: " + jasyptDecrypt(encrypteduserName));
		System.out.println("pw 복호화 :::::::: " + jasyptDecrypt(encryptedPw));
		System.out.println("jwt 복호화 :::::::: " + jasyptDecrypt(encryptedJwtTokenKey));

	    // then
		assertThat(mariaDB_URL).isEqualTo(jasyptDecrypt(encryptedURL));
		assertThat(mariaDB_userName).isEqualTo(jasyptDecrypt(encrypteduserName));
		assertThat(mariaDB_password).isEqualTo(jasyptDecrypt(encryptedPw));
		assertThat(jwtTokenKey).isEqualTo(jasyptDecrypt(encryptedJwtTokenKey));

	}

	private String jasyptEncrypt(String input) {
		String key = "5678";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(key);
		return encryptor.encrypt(input);
	}

	private String jasyptDecrypt(String input){
		String key = "5678";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(key);
		return encryptor.decrypt(input);
	}
}

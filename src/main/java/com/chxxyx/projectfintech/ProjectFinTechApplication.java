package com.chxxyx.projectfintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProjectFinTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectFinTechApplication.class, args);
	}

}

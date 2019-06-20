package com.officeten.omc.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jin
 * @date 2019/6/19
 */
@SpringBootApplication
@ComponentScan("com.officeten.omc")
public class OmcBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(OmcBackendApplication.class, args);
	}
}

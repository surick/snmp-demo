package com.officeten.omc.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jin
 * @date 2019/6/18
 */
@SpringBootApplication
@ComponentScan("com.officeten.omc")
public class OmcNettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(OmcNettyApplication.class, args);
    }
}

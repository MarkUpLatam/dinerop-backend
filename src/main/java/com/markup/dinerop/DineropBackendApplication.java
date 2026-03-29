package com.markup.dinerop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class DineropBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DineropBackendApplication.class, args);
    }

}

package com.example.practika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class PractikaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PractikaApplication.class, args);
    }

}

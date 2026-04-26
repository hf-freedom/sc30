package com.campus.visitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampusVisitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusVisitorApplication.class, args);
    }
}

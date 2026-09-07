package com.skillseed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkillseedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillseedApplication.class, args);
    }
}
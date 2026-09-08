package com.skillseed;

import com.skillseed.session.client.DailyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DailyProperties.class)
public class SkillseedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillseedApplication.class, args);
    }
}
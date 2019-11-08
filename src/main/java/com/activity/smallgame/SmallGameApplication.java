package com.activity.smallgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmallGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmallGameApplication.class, args);
    }

}

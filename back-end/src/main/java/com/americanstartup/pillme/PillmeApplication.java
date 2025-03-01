package com.americanstartup.pillme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PillmeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PillmeApplication.class, args);
    }

}

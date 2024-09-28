package com.tsh.slt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableEurekaClient
@EnableAsync
@SpringBootApplication
public class SltAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SltAgentApplication.class, args);
    }

}

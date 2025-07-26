package com.M1.karts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class KartsServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(com.M1.karts.KartsServiceApp.class, args);
    }

}
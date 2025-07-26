package com.M1.rack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RackServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(com.M1.rack.RackServiceApp.class, args);
    }

}
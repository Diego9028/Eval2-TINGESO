package com.M1.tarifaEsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TarifaEspServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(TarifaEspServiceApp.class, args);
    }

}
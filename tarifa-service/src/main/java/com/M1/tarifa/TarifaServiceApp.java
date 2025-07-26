package com.M1.tarifa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TarifaServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(com.M1.tarifa.TarifaServiceApp.class, args);
    }

}
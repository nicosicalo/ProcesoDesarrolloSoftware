package com.escrims.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.escrims")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

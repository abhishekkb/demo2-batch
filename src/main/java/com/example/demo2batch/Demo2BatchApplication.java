package com.example.demo2batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Demo2BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(Demo2BatchApplication.class, args);
    }

}

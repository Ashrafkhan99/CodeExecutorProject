package com.coderank.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CoderankExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoderankExecutorApplication.class, args);
    }

}

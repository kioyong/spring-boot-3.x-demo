package com.yong.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class TestDemoApplication {

    @Bean



    public static void main(String[] args) {
        SpringApplication
                .from(DemoApplication::main)
                .with(TestDemoApplication.class)
                .run(args);
    }

}

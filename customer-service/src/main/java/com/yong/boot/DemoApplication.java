package com.yong.boot;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class DemoApplication {

	@Generated
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

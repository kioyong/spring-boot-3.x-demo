package com.yong.boot.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "greetingClient", url = "${customer.greeting.url}", path = "/greetingPro")
public interface GreetingClient {
    @GetMapping()
    Greeting greeting();
}

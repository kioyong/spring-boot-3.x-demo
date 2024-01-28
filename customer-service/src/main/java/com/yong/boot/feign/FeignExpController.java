package com.yong.boot.feign;

import com.yong.boot.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/greetingExp")
@RequiredArgsConstructor
public class FeignExpController {

    private final GreetingClient client;

    @GetMapping()
    public Greeting greeting() {
        LogUtils.info(log, "start call greeting in exp");
        Greeting greeting = client.greeting();
        LogUtils.info(log, "end call greeting in exp");
        return greeting;
    }

}


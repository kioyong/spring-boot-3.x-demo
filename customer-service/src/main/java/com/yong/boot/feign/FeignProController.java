package com.yong.boot.feign;

import com.yong.boot.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@Log4j2
@RestController
@RequestMapping("/greetingPro")
public class FeignProController {

    @GetMapping()
    public Greeting greeting(HttpServletRequest request) {
        LogUtils.info(log, "start call greeting in pro");
        Greeting greeting = new Greeting("ABC", "Hello");
        LogUtils.info(log, "end call greeting in pro");
        return greeting;
    }

}


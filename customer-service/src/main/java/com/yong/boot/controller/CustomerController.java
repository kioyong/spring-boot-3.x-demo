package com.yong.boot.controller;


import com.yong.boot.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping("/customers")
    List<Customer> customers() {
        log.info("start get customers");
        return service.findAll();
    }

    @GetMapping("/customers/{name}")
    List<Customer> findByName(@PathVariable String name) {
        log.info("start find by name controller");
        return service.findByName(name);
    }


    @PostMapping("/customers")
    Customer save(@RequestBody Customer customer) {
        log.info("start save customer");
        return service.save(customer);
    }


    @GetMapping("/testAsync")
    String test(){
        log.info("start aaa");
        service.testAsync();
        log.info("end aaa");
        return "done";
    }
}

package com.yong.boot.controller;

import com.yong.boot.entity.Customer;
import com.yong.boot.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    List<Customer> customers() {
        log.info("start get customers");
        return customerRepository.findAll();
    }

    @GetMapping("/customers/{name}")
    List<Customer> findByName(@PathVariable String name) {
        return customerRepository.findByName(name);
    }
}

package com.example.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RequestMapping("/customers")
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping("/{id}")
    Mono<Customer> findById(@PathVariable String id) {
        log.info("start find customer by id : {} ", id);
        return service.findById(id);
    }

    @GetMapping()
    Flux<Customer> customers() {
        log.info("start find all customer");
        return service.findAll();
    }

    @PostMapping
    Mono<Customer> save(@RequestBody Customer customer) {
        log.info("start save customer");
        return service.save(customer);
    }
}

package com.example.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    public Mono<Customer> findById(String id) {
        return repository.findById(id);
    }

    public Flux<Customer> findAll() {
        return repository.findAll();
    }

    public Mono<Customer> save(Customer customer) {
        return repository.save(customer);
    }
}

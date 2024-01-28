package com.yong.boot.customer;

import brave.baggage.BaggageField;
import brave.propagation.CurrentTraceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.yong.boot.util.LogUtils.application;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    private final CurrentTraceContext context;

    public List<Customer> findAll() {
        BaggageField.create("biz_keys").updateValue("{\"userId\":\"456\"}");
        log.info(application, "start findAll");
        return repository.findAll();
    }


    public List<Customer> findByNameOther(String name) {
        log.info(application, "start find by name service {}", name);
        return repository.findByName(name);
    }

    public Customer save(Customer customer) {
        log.info(application, "start call save repository");
        return repository.save(customer);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Customer>> findByName(String name) {
        log.info(application,"start find by Name Async");
        return CompletableFuture.completedFuture(repository.findByName(name));
    }


}

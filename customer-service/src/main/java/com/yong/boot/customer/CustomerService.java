package com.yong.boot.customer;

import brave.baggage.BaggageField;
import brave.propagation.CurrentTraceContext;
import com.yong.boot.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.yong.boot.constant.BusinessConstant.BIZ_KEYS;
import static com.yong.boot.util.LogUtils.application;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    private final CurrentTraceContext context;

    public List<Customer> findAll() {
        BaggageField.create(BIZ_KEYS).updateValue("{\"userId\":\"456\"}");
        LogUtils.info(log, "start findAll");
        return repository.findAll();
    }


    public List<Customer> findByName(String name) {
        LogUtils.info(log, "start find by name service {}", name);
        return repository.findByName(name);
    }

    public Customer save(Customer customer) {
        LogUtils.info(log, "start call save repository");
        return repository.save(customer);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Customer>> findByNameAsync(String name) {
        LogUtils.info(log,"start find by Name Async");
        return CompletableFuture.completedFuture(repository.findByName(name));
    }


}

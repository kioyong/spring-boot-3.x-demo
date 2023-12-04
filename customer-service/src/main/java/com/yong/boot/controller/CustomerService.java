package com.yong.boot.controller;

import brave.baggage.BaggageField;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import com.yong.boot.entity.Customer;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    private final CurrentTraceContext context;

    public List<Customer> findAll() {
        BaggageField.getByName("contextId").updateValue("after");
        BaggageField.getByName("businessKeys").updateValue("{\"userId\":\"123\"}");
        log.info("start findAll");
        return repository.findAll();
    }


    public List<Customer> findByName(@SpanTag("contextIdBBB") String name) {
        log.info("start find by name service {}", name);
        TraceContext traceContext = context.get();
        return repository.findByName(name);
    }

    public Customer save(Customer customer) {
        log.info("start call save repository");
        return repository.save(customer);
    }

    @Observed(name = "xxx",contextualName = "ddd",lowCardinalityKeyValues = {"abc","def"})
    public void testAsync() {
        log.info("stact async");
    }
}

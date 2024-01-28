package com.yong.boot.customer;


import brave.baggage.BaggageField;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.yong.boot.constant.BusinessConstant.BIZ_FUNC;
import static com.yong.boot.util.LogUtils.*;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;


    @GetMapping("/customers")
    List<Customer> customers(HttpServletRequest request) {
        BaggageField.create(BIZ_FUNC).updateValue("findAllCustomers");
        String traceId = BaggageField.getByName("trace_id").getValue();
        log.info(securityAudit, "start get customers1 {}", traceId);
        log.info(application, "start get customers2 {}", traceId);
        log.info(integration, "start get customers3 {}", traceId);
        return service.findAll();
    }

    @GetMapping("/customers/{name}")
    List<Customer> findByName(@PathVariable String name) {
        BaggageField.create(BIZ_FUNC).updateValue("findCustomersByName");
        updateBusinessKey("customerName",name);
        log.info(application, "start find by name controller");
        return service.findByName(name);
    }


    @PostMapping("/customers")
    Customer save(@RequestBody Customer customer) {
        log.info(application, "start save customer");
        return service.save(customer);
    }


    @GetMapping("/testAsync")
    List<Customer> test() throws ExecutionException, InterruptedException {
        BaggageField.create(BIZ_FUNC).updateValue("testAsync");
        log.info(application, "start aaa");
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(servletRequestAttributes)) {
            RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);
        }
        CompletableFuture<List<Customer>> task1 = service.findByNameAsync("liangyong");
        CompletableFuture<List<Customer>> task2 = service.findByNameAsync("liangyongs");
        CompletableFuture<List<Customer>> task3 = service.findByNameAsync("liangyonxx");
        CompletableFuture.allOf(task1, task2, task3)
                .join();

        List<Customer> customer = task1.get();
        List<Customer> customer1 = task2.get();
        List<Customer> customer2 = task3.get();
        customer.addAll(customer1);
        customer.addAll(customer2);
        log.info(application, "end aaa");
        return customer;
    }


}

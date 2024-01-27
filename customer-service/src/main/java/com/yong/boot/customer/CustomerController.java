package com.yong.boot.customer;


import brave.baggage.BaggageField;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.yong.boot.util.LogUtils.*;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;


    @GetMapping("/customers")
    List<Customer> customers(HttpServletRequest request) {
        BaggageField.create("biz_func").updateValue("findAllCustomers");
        String myTraceId = request.getHeader("myTraceId");

        log.info(securityAudit, "start get customers1 {}", myTraceId);
        log.info(application, "start get customers2 {}", myTraceId);
        log.info(integration, "start get customers3 {}", myTraceId);
        return service.findAll();
    }

    @GetMapping("/customers/{name}")
    List<Customer> findByName(@PathVariable String name) {
        BaggageField.create("biz_func").updateValue("findCustomersByName");
        log.info(application, "start find by name controller");
        return service.findByNameOther(name);
    }


    @PostMapping("/customers")
    Customer save(@RequestBody Customer customer) {
        log.info(application, "start save customer");
        return service.save(customer);
    }


    @GetMapping("/testAsync")
    List<Customer> test() throws ExecutionException, InterruptedException {
        log.info(application, "start aaa");
        CompletableFuture<List<Customer>> task1 = service.findByName("a");
        CompletableFuture<List<Customer>> task2 = service.findByName("b");
        CompletableFuture<List<Customer>> task3 = service.findByName("c");
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

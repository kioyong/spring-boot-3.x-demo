package com.yong.boot.customer;


import brave.baggage.BaggageField;
import com.yong.boot.util.LogUtils;
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
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;


    @GetMapping()
    List<Customer> findAllCustomers() {
//        String traceId = BaggageField.getByName("trace_id").getValue();
        LogUtils.info(log, "start get customers1");
        LogUtils.info(log, securityAudit, "test security audit log");
        LogUtils.info(log, integration, "test integration log");
        return service.findAll();
    }

    @GetMapping("/{name}")
    List<Customer> findCustomersByName(@PathVariable String name) {
        LogUtils.updateBusinessKey("customerName", name);
        LogUtils.info(log, "start find by name controller");
        return service.findByName(name);
    }


    @PostMapping()
    Customer saveCustomers(@RequestBody Customer customer) {
        LogUtils.info(log, "start save customer");
        return service.save(customer);
    }


    @GetMapping("/testAsync")
    List<Customer> testAsync() throws ExecutionException, InterruptedException {
        LogUtils.info(log, "start aaa");
        CompletableFuture<List<Customer>> task1 = service.findByNameAsync("task1");
        CompletableFuture<List<Customer>> task2 = service.findByNameAsync("task2");
        CompletableFuture<List<Customer>> task3 = service.findByNameAsync("task3");
        CompletableFuture.allOf(task1, task2, task3)
                .join();

        List<Customer> customer = task1.get();
        List<Customer> customer1 = task2.get();
        List<Customer> customer2 = task3.get();
        customer.addAll(customer1);
        customer.addAll(customer2);
        LogUtils.info(log, "end c");
        return customer;
    }


}

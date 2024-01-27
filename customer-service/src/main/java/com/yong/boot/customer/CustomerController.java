package com.yong.boot.customer;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/sql")
    public Map<String, Object> executeSql(@RequestBody Map<String, String> body) {
        String sql = body.get("sql");
        ResultSetExtractor<Map<String, Object>> rse = rs -> {
            if (rs.next()) {
                Map<String, Object> resultMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resultMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                return resultMap;
            }
            return null;
        };
        try {
            Map<String, Object> result = jdbcTemplate.query(sql, rse);
            // Handle null case
            if (result == null) result = new HashMap<>();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @GetMapping("/customers")
    List<Customer> customers() {
        log.info("start get customers");
        return service.findAll();
    }

    @GetMapping("/customers/{name}")
    List<Customer> findByName(@PathVariable String name) {
        log.info("start find by name controller");
        return service.findByNameOther(name);
    }


    @PostMapping("/customers")
    Customer save(@RequestBody Customer customer) {
        log.info("start save customer");
        return service.save(customer);
    }


    @GetMapping("/testAsync")
    List<Customer> test() throws ExecutionException, InterruptedException {
        log.info("start aaa");
        CompletableFuture<Customer> task1 = service.findByName("a");
        CompletableFuture<Customer> task2 = service.findByName("b");
        CompletableFuture<Customer> task3 = service.findByName("c");
        CompletableFuture.allOf(task1, task2, task3)
                .join();

        Customer customer = task1.get();
        Customer customer1 = task2.get();
        Customer customer2 = task3.get();

        log.info("end aaa");
        return List.of(customer1, customer2, customer);
    }


}

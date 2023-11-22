package com.yong.boot.controller;

import com.yong.boot.entity.Customer;
import com.yong.boot.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CustomerControllerTest {


    @Autowired
    private MockMvc mvc;


    @Autowired
    CustomerRepository repo;


    @Test
    void customers() throws Exception {
        mvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByName() throws Exception {
        assertEquals(0, repo.count());
        Customer customer = new Customer();
        String name = "Dave";
        customer.setName(name);
        customer.setId(1);
        repo.save(customer);
        assertEquals(1, repo.count());

        mvc.perform(get("/customers/Jack"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        mvc.perform(get("/customers/" + name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
package com.yong.boot.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureObservability
class CustomerControllerTest {


    @Autowired
    private MockMvc mvc;


    @Autowired
    CustomerRepository repo;


    @Test
    void customers() throws Exception {
        mvc.perform(setDefaultHeader(get("/customers")))
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

        mvc.perform(setDefaultHeader(get("/customers/Jack")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        mvc.perform(setDefaultHeader(get("/customers/" + name)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    MockHttpServletRequestBuilder setDefaultHeader(MockHttpServletRequestBuilder builder) {
        return builder.header("x-AIAHK-Trace-ID", "traceId")
                .header("x-AIAHK-Context-ID", "contextId")
                .header("x-user-id", "userId")
                .header("x-forwarded-for", "localhost");
    }
}
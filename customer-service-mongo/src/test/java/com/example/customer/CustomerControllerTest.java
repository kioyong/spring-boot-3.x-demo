package com.example.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    WebTestClient client;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerRepository customerRepository;

    @Test
    void findById() {
        Customer customer = new Customer("1", "test");
        when(customerRepository.findById(eq("1"))).thenReturn(Mono.just(customer));
        when(customerRepository.findById(anyString())).thenReturn(Mono.empty());
        client.get().uri("/customers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name", equals("test"));

        client.get().uri("/customers/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void customers() {
        when(customerRepository.findAll()).thenReturn(Flux.empty());
        client.get().uri("/customers")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void save() throws JsonProcessingException {
        Customer customer = new Customer("1", "test");
        when(customerRepository.save(any())).thenReturn(Mono.just(customer));
        client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(customer))
                .exchange()
                .expectStatus().isOk();
    }
}
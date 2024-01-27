package com.example.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@Testcontainers
@DataMongoTest
class CustomerRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    CustomerRepository repository;


    @Test
    void saveRepository() {
        repository.deleteAll();
        var saveAndCount = repository.count()
                .doOnNext(System.out::println)
                .then(repository.save(new Customer("1", "Test")))
                .flatMap(v -> repository.count())
                .doOnNext(System.out::println);

        saveAndCount.as(StepVerifier::create).expectNext(1L).verifyComplete();
    }

    @Test
    void saveRepository2() {
        repository.deleteAll();
        var saveAndCount = repository.count()
                .doOnNext(System.out::println)
                .then(repository.save(new Customer("1", "Test")))
                .flatMap(v -> repository.count())
                .doOnNext(System.out::println);

        saveAndCount.as(StepVerifier::create).expectNext(1L).verifyComplete();
    }

}
package com.rkd.auto.config;

import com.rkd.auto.container.KafkaContainer;
import com.rkd.auto.container.PostgresContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class TestConfig {

    @Bean
    public PostgresContainer postgresContainer() {
        return new PostgresContainer();
    }

    @Bean
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer();
    }
}

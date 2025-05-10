package com.rkd.auto.container;

import com.rkd.auto.config.ContainerConfig;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

@Component
public class PostgresContainer implements ContainerConfig {

    private final PostgreSQLContainer<?> postgresSqlContainer;

    public PostgresContainer() {
        this.postgresSqlContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("auto-park-system")
                .withUsername(UUID.randomUUID().toString())
                .withPassword(UUID.randomUUID().toString());

        this.postgresSqlContainer.start();
    }

    @Override
    public void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresSqlContainer::getPassword);
    }

    @Override
    public void close() {
        postgresSqlContainer.stop();
    }
}

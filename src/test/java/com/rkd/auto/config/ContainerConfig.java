package com.rkd.auto.config;

import jakarta.annotation.PreDestroy;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface ContainerConfig {
    @DynamicPropertySource
    void configureProperties(DynamicPropertyRegistry registry);

    @PreDestroy
    void close();
}

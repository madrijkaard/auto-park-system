package com.rkd.auto.container;

import com.rkd.auto.config.ContainerConfig;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.utility.DockerImageName;

@Component
public class KafkaContainer implements ContainerConfig {

    private final org.testcontainers.containers.KafkaContainer kafkaContainer;

    public KafkaContainer() {
        kafkaContainer = new org.testcontainers.containers.KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));
        kafkaContainer.start();
    }

    @Override
    public void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Override
    public void close() {
        kafkaContainer.stop();
    }
}

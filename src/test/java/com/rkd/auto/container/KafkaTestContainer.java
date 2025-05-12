package com.rkd.auto.container;

import com.rkd.auto.config.ContainerConfig;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Component
public class KafkaTestContainer implements ContainerConfig {

    private final KafkaContainer kafkaContainer;

    public KafkaTestContainer() {
        this.kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));
        this.kafkaContainer.start();
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

package com.marintoma.taskqueue.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${taskqueue.kafka.topic}")
    private String topic;

    @Value("${taskqueue.kafka.partitions}")
    private int partitions;

    @Value("${taskqueue.kafka.replication-factor}")
    private short replicationFactor;

    @Bean
    public NewTopic taskExecutionTopic() {
        return TopicBuilder
                .name(topic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}

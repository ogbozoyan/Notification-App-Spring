package com.example.producersvc.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ogbozoyan
 * @since 12.09.2023
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.port}")
    private String kafkaPort;
    @Value("${kafka.consumer.group.id}")
    private String kafkaConsumerGroupId;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer + ":" + kafkaPort);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "3000");  //time between pings to kafka
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "12000"); //max delay between pings
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1"); //value of records in one request
        configProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed"); // read only committed

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}

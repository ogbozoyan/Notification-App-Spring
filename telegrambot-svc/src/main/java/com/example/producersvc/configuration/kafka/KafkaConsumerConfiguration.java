package com.example.producersvc.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class KafkaConsumerConfiguration {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.port}")
    private String kafkaPort;
    @Value("${kafka.consumer.group.id}")
    private String kafkaConsumerGroupId;

    @Bean
    public ConsumerFactory<Long, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        String bootStrapServer = kafkaServer + ":" + kafkaPort;
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "3000");  //time between pings to kafka
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "12000"); //max delay between pings
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1"); //value of records in one request
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed"); // read only committed

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}

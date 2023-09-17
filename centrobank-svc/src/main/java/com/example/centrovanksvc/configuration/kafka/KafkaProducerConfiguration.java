package com.example.centrovanksvc.configuration.kafka;

import com.example.centrovanksvc.web.dto.KafkaProducerDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ogbozoyan
 * @since 17.09.2023
 */
@Configuration
public class KafkaProducerConfiguration {
    @Value("${kafka.server}")
    private String kafkaIp;
    @Value("${kafka.port}")
    private String kafkaPort;
    @Value("${kafka.topic}")
    private String kafkaTopic;
    @Value("${kafka.producer}")
    private String kafkaProducer;

    @Bean //String - key, Value - json which will be sent to kafka
    public ProducerFactory<String, List<KafkaProducerDTO>> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        String bootStrapServers = kafkaIp + ":" + kafkaPort;
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, List<KafkaProducerDTO>> kafkaTemplate() {
        KafkaTemplate<String, List<KafkaProducerDTO>> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(kafkaTopic);
        kafkaTemplate.setMessageConverter(new StringJsonMessageConverter());
        return kafkaTemplate;
    }
}

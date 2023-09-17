package com.example.producersvc.service.kafka;

import com.example.producersvc.service.bot.BotService;
import com.example.producersvc.web.dto.KafkaMessageFromProducerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ogbozoyan
 * @since 15.09.2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumer {

    private final BotService botService;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.consumer.group.id}")
    public void receive(ConsumerRecord<Long, String> data) {
        TypeFactory typeFactory = mapper.getTypeFactory();

        List<KafkaMessageFromProducerDTO> dto;
        try {
            dto = mapper.readValue(data.value(), typeFactory.constructCollectionType(List.class, KafkaMessageFromProducerDTO.class));

            log.info("Received from producer: {}", String.join("\n", dto.stream().map(KafkaMessageFromProducerDTO::toString).toList()));
            botService.sendToUsers(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.producersvc.service.kafka;

import com.example.producersvc.service.bot.BotService;
import com.example.producersvc.web.dto.ProducerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Collections;

/**
 * @author ogbozoyan
 * @since 15.09.2023
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final BotService botService;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.consumer.group.id}")
    public void receive(ProducerDTO dto) {
        log.info("Received: {}", dto);
        botService.sendToUsers(Collections.singletonList(dto));

    }
}

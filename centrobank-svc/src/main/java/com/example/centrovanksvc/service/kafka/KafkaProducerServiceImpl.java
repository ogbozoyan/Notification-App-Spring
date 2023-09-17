package com.example.centrovanksvc.service.kafka;

import com.example.centrovanksvc.configuration.feign.FreeCurrencyFeignClient;
import com.example.centrovanksvc.model.CurrencyEntity;
import com.example.centrovanksvc.model.UserEntity;
import com.example.centrovanksvc.repository.UserEntityRepository;
import com.example.centrovanksvc.service.currency.CurrencyEntityService;
import com.example.centrovanksvc.web.dto.CurrencyDTO;
import com.example.centrovanksvc.web.dto.FreeCurrencyApiResponseDTO;
import com.example.centrovanksvc.web.dto.KafkaProducerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ogbozoyan
 * @since 16.09.2023
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
    @Value("${services.freecurrency.token}")
    private String apiToken;
    @Value("${kafka.topic}")
    private String kafkaTopic;

    private final FreeCurrencyFeignClient freeCurrencyFeignClient;
    private final CurrencyEntityService currencyEntityService;
    private final UserEntityRepository userEntityRepository;
    private final KafkaTemplate<String, List<KafkaProducerDTO>> kafkaTemplate;


    public static final List<String> CURRENCIES = new ArrayList<>();

    static {
        CURRENCIES.add("USD");
        CURRENCIES.add("CNY");
        CURRENCIES.add("EUR");
    }


    /**
     * Method to send a message to kafka
     * ┌───────────── second (0-59)
     *  │ ┌───────────── minute (0 - 59)
     *  │ │ ┌───────────── hour (0 - 23)
     *  │ │ │ ┌───────────── day of the month (1 - 31)
     *  │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
     *  │ │ │ │ │ ┌───────────── day of the week (0 - 7)
     *  │ │ │ │ │ │          (or MON-SUN -- 0 or 7 is Sunday)
     *  │ │ │ │ │ │
     *  * * * * * *
     */
    @Override
//    @Scheduled(cron = "0/10 * * * * ?") //every 10 seconds
    @Scheduled(cron = "* 30 7/1 * * * ?") //every hour since 7:30
    public void sendMessage() {
        Set<UserEntity> allSubscribers = userEntityRepository.findAllWhereSubChatORSubMail();
        try {
            List<KafkaProducerDTO> dto = allSubscribers.stream().map(userEntity -> {
                        KafkaProducerDTO kafkaProducerDTO = new KafkaProducerDTO();

                        CurrencyDTO currencyDTO = new CurrencyDTO();
                        for (String currency : CURRENCIES) {
                            FreeCurrencyApiResponseDTO apiResponseDTO = freeCurrencyFeignClient.getCurrency(apiToken, "RUB", currency);
                            currencyDTO.setCurrency(currency, apiResponseDTO.getPrice());

                            CurrencyEntity currencyEntity = currencyEntityService.getByCurrencyName(currency);
                            currencyEntityService.updatePrices(currencyEntity.getName(), Double.valueOf(apiResponseDTO.getPrice()));

                        }
                        kafkaProducerDTO.setUserId(userEntity.getId());
                        kafkaProducerDTO.setCurrencyDTO(currencyDTO);
                        return kafkaProducerDTO;
                    }
            ).toList();

            if (dto.isEmpty()) {
                log.info("No subscribers");
                return;
            }

            kafkaTemplate.send(kafkaTopic, dto);
            log.info("Sent message to kafka topic: {}, to users {}", kafkaTopic, allSubscribers.stream().map(sub ->"User id: "+sub.getId()+" mail: "+sub.getEmail()+"; ").toList());
        } catch (Exception e) {
            log.info("Failed to sent message to kafka topic: {}, to users {}", kafkaTopic, allSubscribers.stream().map(sub ->"User id: "+sub.getId()+" mail: "+sub.getEmail()+"; ").toList());
            throw new RuntimeException(e);
        }

    }
}

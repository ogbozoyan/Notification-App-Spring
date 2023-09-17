package com.example.centrovanksvc.web.controller;

import com.example.centrovanksvc.configuration.feign.FreeCurrencyFeignClient;
import com.example.centrovanksvc.model.CurrencyEntity;
import com.example.centrovanksvc.service.currency.CurrencyEntityService;
import com.example.centrovanksvc.web.dto.CurrencyDTO;
import com.example.centrovanksvc.web.dto.FreeCurrencyApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.centrovanksvc.service.kafka.KafkaProducerServiceImpl.CURRENCIES;

/**
 * @author ogbozoyan
 * @since 17.09.2023
 */
@RestController("web")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class WebController {
    @Value("${services.freecurrency.token}")
    private String apiToken;

    @Autowired
    private FreeCurrencyFeignClient freeCurrencyFeignClient;
    @Autowired
    private CurrencyEntityService currencyEntityService;

    @GetMapping("/get-currency")
    public CurrencyDTO getCurrency() {

        CurrencyDTO currencyDTO = new CurrencyDTO();
        for (String currency : CURRENCIES) {
            FreeCurrencyApiResponseDTO apiResponseDTO = freeCurrencyFeignClient.getCurrency(apiToken, "RUB", currency);
            currencyDTO.setCurrency(currency, apiResponseDTO.getPrice());

            CurrencyEntity currencyEntity = currencyEntityService.getByCurrencyName(currency);
            currencyEntityService.updatePrices(currencyEntity.getName(), Double.valueOf(apiResponseDTO.getPrice()));

        }


        return currencyDTO;
    }
}

package com.example.centrovanksvc.configuration.feign;

import com.example.centrovanksvc.web.dto.FreeCurrencyApiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ogbozoyan
 * @since 16.09.2023
 */
@FeignClient(name = "freeCurrencyFeignCLient", configuration = FeignClientConfiguration.class, url = "${services.freecurrency.url}")
public interface FreeCurrencyFeignClient {

    @GetMapping(value = "latest")
    FreeCurrencyApiResponseDTO getCurrency(@RequestParam(name = "apikey") String apiKey,
                                           @RequestParam(name = "currencies") String currencies,
                                           @RequestParam(name = "base_currency") String base);
}
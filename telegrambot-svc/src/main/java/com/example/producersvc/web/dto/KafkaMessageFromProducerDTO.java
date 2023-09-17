package com.example.producersvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ogbozoyan
 * @since 15.09.2023
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessageFromProducerDTO implements Serializable {
    private Long userId;
    private CurrencyDTO currencyDTO;

    @Override
    public String toString() {
        return "Producer sent: " +
                "userId: " + userId +
                ", currencies: " + currencyDTO +
                '}';
    }
}

package com.example.centrovanksvc.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ogbozoyan
 * @since 17.09.2023
 */
@Data
@NoArgsConstructor
public class KafkaProducerDTO implements Serializable {
    private Long userId;
    private CurrencyDTO currencyDTO;
}

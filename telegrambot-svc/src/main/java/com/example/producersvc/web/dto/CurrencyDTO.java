package com.example.producersvc.web.dto;

import lombok.*;

import java.io.Serializable;


/**
 * @author ogbozoyan
 * @since 16.09.2023
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyDTO implements Serializable {
    private String usd;
    private String cny;
    private String eur;

    @Override
    public String toString() {
        return "{" +
                "USD='" + usd + '\'' +
                ", CNY='" + cny + '\'' +
                ", EUR='" + eur + '\'' +
                '}';
    }
}

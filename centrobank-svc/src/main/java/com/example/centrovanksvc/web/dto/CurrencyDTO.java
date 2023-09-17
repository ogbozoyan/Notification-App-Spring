package com.example.centrovanksvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author ogbozoyan
 * @since 16.09.2023
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyDTO implements Serializable {
    private String usd;
    private String cny;
    private String eur;

    public void setCurrency(String currency, String price) {
        if (currency.equalsIgnoreCase("USD"))
            this.usd = price;
        else if (currency.equalsIgnoreCase("CNY"))
            this.cny = price;
        else if (currency.equalsIgnoreCase("EUR"))
            this.eur = price;
    }

    public String getCurrencyPrices() {
        return "USD: " + usd + "\n" + "CNY: " + cny + "\n " + "EUR: " + eur;
    }

}

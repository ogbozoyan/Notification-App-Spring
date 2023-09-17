package com.example.centrovanksvc.service.currency;

import com.example.centrovanksvc.model.CurrencyEntity;

/**
 * @author ogbozoyan
 * @since 17.09.2023
 */
public interface CurrencyEntityService {
    CurrencyEntity getByCurrencyName(String currencyName);
    CurrencyEntity updatePrices(String currencyName, Double newPrice);
}

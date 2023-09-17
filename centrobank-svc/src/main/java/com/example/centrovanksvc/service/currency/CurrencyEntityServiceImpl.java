package com.example.centrovanksvc.service.currency;

import com.example.centrovanksvc.model.CurrencyEntity;
import com.example.centrovanksvc.repository.CurrencyEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author ogbozoyan
 * @since 17.09.2023
 */
@Service
@RequiredArgsConstructor
public class CurrencyEntityServiceImpl implements CurrencyEntityService {
    private final CurrencyEntityRepository repository;

    @Override
    public CurrencyEntity getByCurrencyName(String currencyName) {
        return repository.findByName(currencyName);
    }

    @Override
    public CurrencyEntity updatePrices(String currencyName, Double newPrice) {
        CurrencyEntity currencyEntity = this.getByCurrencyName(currencyName);

        Double previousPrice = currencyEntity.getPreviousPrice();
        Double currentPrice = currencyEntity.getCurrentPrice();

        if(currentPrice == null){
            currencyEntity.setCurrentPrice(newPrice);
        }else {
            currencyEntity.setPreviousPrice(currentPrice);
            currencyEntity.setCurrentPrice(newPrice);
        }

        return repository.saveAndFlush(currencyEntity);
    }
}

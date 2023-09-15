package com.example.producersvc.repository;

import com.example.producersvc.model.CurrencyEntity;
import com.example.producersvc.model.UserEntity;
import com.example.producersvc.model.UserLCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserLCurrencyRepository extends JpaRepository<UserLCurrency, Integer> {
    @Transactional
    UserLCurrency findByUserAndCurrency(UserEntity user, CurrencyEntity currency);

    @Transactional
    void deleteByUser(UserEntity user);
}
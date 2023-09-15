package com.example.producersvc.repository;

import com.example.producersvc.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyEntityRepository extends JpaRepository<CurrencyEntity, Long> {
    CurrencyEntity findByName(String messageFromCon);
}
package com.example.producersvc.repository;

import com.example.producersvc.model.EmailConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfirmationEntityRepository extends JpaRepository<EmailConfirmationEntity, Long> {
    EmailConfirmationEntity findByEmail(String email);
}
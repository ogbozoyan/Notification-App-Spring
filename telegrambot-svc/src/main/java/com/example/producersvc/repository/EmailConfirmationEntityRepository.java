package com.example.producersvc.repository;

import com.example.producersvc.model.EmailConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EmailConfirmationEntityRepository extends JpaRepository<EmailConfirmationEntity, Long> {
    @Transactional(readOnly = true)
    EmailConfirmationEntity findByEmail(String email);
}
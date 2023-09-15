package com.example.producersvc.repository;

import com.example.producersvc.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ogbozoyan
 * @since 09.09.2023
 */
@Repository
public interface UserReporitory extends JpaRepository<UserEntity, Long> {
    UserEntity findByChatId(String chatId);
}

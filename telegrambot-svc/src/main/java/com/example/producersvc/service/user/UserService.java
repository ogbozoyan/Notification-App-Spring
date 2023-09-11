package com.example.producersvc.service.user;

import com.example.producersvc.model.UserEntity;

import java.util.List;

/**
 * @author ogbozoyan
 * @since 09.09.2023
 */

public interface UserService {
    UserEntity save(UserEntity user);

    UserEntity update(UserEntity user);

    UserEntity findById(Long id);

    List<UserEntity> findAll();

    void delete(Long id);

    UserEntity findByChatId(String chatId);
}

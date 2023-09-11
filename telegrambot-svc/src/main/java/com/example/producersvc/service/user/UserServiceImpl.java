package com.example.producersvc.service.user;

import com.example.producersvc.model.UserEntity;
import com.example.producersvc.repository.UserReporitory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ogbozoyan
 * @since 09.09.2023
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserReporitory repository;
    @Autowired
    protected ModelMapper patchingMapper;

    @Override
    public UserEntity save(UserEntity user) {
        return repository.saveAndFlush(user);
    }

    @Override
    public UserEntity update(UserEntity user) {
        UserEntity entityFromDB = repository.findById(user.getId()).orElseThrow();
        patchingMapper.map(user, entityFromDB);
        return repository.saveAndFlush(entityFromDB);
    }

    @Override
    public UserEntity findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public List<UserEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserEntity findByChatId(String chatId) {
        return repository.findByChatId(chatId);
    }

}

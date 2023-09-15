package com.example.producersvc.repository;

import com.example.producersvc.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
    @Transactional(readOnly = true)
    UserState findByName(String name);
}
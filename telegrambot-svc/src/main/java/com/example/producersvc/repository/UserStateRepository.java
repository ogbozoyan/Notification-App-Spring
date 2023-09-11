package com.example.producersvc.repository;

import com.example.producersvc.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
    UserState findByName(String name);
}
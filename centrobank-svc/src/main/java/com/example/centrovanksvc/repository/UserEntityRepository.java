package com.example.centrovanksvc.repository;

import com.example.centrovanksvc.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = """
            SELECT DISTINCT u FROM UserEntity u
            WHERE u.subChat = true OR u.subEmail = true
            """)
    Set<UserEntity> findAllWhereSubChatORSubMail();
}
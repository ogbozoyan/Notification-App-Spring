package com.example.centrovanksvc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sub_chat")
    private Boolean subChat;

    @Column(name = "sub_email")
    private Boolean subEmail;

    @Column(name = "chat_id", length = Integer.MAX_VALUE)
    private String chatId;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

}
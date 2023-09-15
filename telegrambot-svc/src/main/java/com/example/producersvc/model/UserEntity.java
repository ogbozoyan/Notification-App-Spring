package com.example.producersvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"user\"", schema = "public")
@EqualsAndHashCode
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", length = Integer.MAX_VALUE)
    @Email
    private String email;

    @Column(name = "chat_id", length = Integer.MAX_VALUE)
    private String chatId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_l_currency",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "currency_id"))
    private Set<CurrencyEntity> currencyEntities;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private UserState stateId;

    @Column(name = "sub_email")
    private Boolean subMail;
    @Column(name = "sub_chat")
    private Boolean subChat;


}
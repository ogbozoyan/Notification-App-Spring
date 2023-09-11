package com.example.producersvc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "email_confirmation")
public class EmailConfirmationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(name = "code", length = Integer.MAX_VALUE)
    private String code;

}
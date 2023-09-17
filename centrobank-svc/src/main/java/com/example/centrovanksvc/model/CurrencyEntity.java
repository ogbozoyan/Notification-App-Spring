package com.example.centrovanksvc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "currency")
public class CurrencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "previous_price")
    private Double previousPrice;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

}
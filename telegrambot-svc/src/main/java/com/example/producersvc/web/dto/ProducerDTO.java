package com.example.producersvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author ogbozoyan
 * @since 15.09.2023
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProducerDTO implements Serializable {
    private Long userId;
    private String message;
}

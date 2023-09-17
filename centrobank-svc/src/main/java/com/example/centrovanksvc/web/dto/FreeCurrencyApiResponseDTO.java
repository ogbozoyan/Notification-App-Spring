package com.example.centrovanksvc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ogbozoyan
 * @since 17.09.2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeCurrencyApiResponseDTO implements Serializable {
    private DataDTO data;
    public String getPrice(){
        return String.valueOf(data.getRub());
    }
}
@Data
@NoArgsConstructor
@AllArgsConstructor
class DataDTO implements Serializable{
    @JsonProperty("RUB")
    public double rub;
}

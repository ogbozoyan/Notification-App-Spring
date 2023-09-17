package com.example.centrovanksvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableKafka
public class CentrobankSvc {
    //https://www.cbr.ru/development/SXML/
    //As realtime currency check service I've chosen https://api.freecurrencyapi.com/v1/,5000 free requests, for educational purposes sounds well :)

    public static void main(String[] args) {
        SpringApplication.run(CentrobankSvc.class, args);
    }

}

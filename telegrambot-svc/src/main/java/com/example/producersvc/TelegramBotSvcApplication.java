package com.example.producersvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class TelegramBotSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotSvcApplication.class, args);
    }

}

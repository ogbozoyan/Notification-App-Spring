package com.example.producersvc.web.controller;

import com.example.producersvc.service.bot.BotService;
import com.example.producersvc.web.dto.ProducerDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ogbozoyan
 * @since 15.09.2023
 */
@RestController
@RequestMapping("/telegram")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Telegram bot controller")
@RequiredArgsConstructor
@Slf4j
public class TelegramController {
    private final BotService botService;

    @PostMapping("/send-to-users")
    @Tag(name = "Рассылка пользователей которые подписаны на рассылку")
    public void sendToUsers(@RequestBody List<ProducerDTO> userList) {
        botService.sendToUsers(userList);
    }
}

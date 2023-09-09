package com.example.producersvc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author ogbozoyan
 * @since 09.09.2023
 */
@Service
public class BotService extends TelegramLongPollingBot {

    public BotService(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    /**
     * This method is called when receiving updates via GetUpdates method
     *
     * @param update Update received
     */
    @Override
    public void onUpdateReceived(Update update) {

    }

    /**
     * Return username of this bot
     */
    @Override
    public String getBotUsername() {
        return null;
    }


}

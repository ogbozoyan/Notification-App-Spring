package com.example.producersvc.service.bot;

import com.example.producersvc.model.EmailConfirmationEntity;
import com.example.producersvc.model.UserEntity;
import com.example.producersvc.model.UserState;
import com.example.producersvc.repository.EmailConfirmationEntityRepository;
import com.example.producersvc.repository.UserStateRepository;
import com.example.producersvc.service.mail.EmailService;
import com.example.producersvc.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

/**
 * @author ogbozoyan
 * @since 09.09.2023
 */
@Service
@Slf4j
public class BotService extends AbilityBot {

    private final String botToken;

    private final String botUsername;
    private final String MAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailConfirmationEntityRepository emailConfirmationEntityRepository;
    @Autowired
    private UserStateRepository userStateRepository;

    @PostConstruct
    public void init() {
        log.info("Token: {}", botToken);
        log.info("Name: {}", botUsername);
    }

    @Autowired
    public BotService(TelegramBotsApi telegramBotsApi, @Value("${bot.name}") String botUsername, @Value("${bot.token}") String botToken) throws TelegramApiException {
        super(botToken, botUsername);
        this.botUsername = botUsername;
        this.botToken = botToken;

        telegramBotsApi.registerBot(this);
    }

    public Ability start() {
        return Ability.builder().name("start").info(Constants.START_DESCRIPTION).locality(ALL).privacy(PUBLIC).action(ctx -> silent.send("Welcome to bot, need to registration to continue /reg", ctx.chatId())).build();
    }

    public Ability registration() {
        return Ability.builder()
                .name("reg")
                .info(Constants.CHAT_REGISTRATION_STATE)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::contextHandler)
                .build();
    }

    public Ability codeConfirm() {
        return Ability.builder()
                .name("code")
                .info(Constants.CHAT_REGISTRATION_STATE)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::contextHandler)
                .build();
    }

    public Ability resendCode() {
        return Ability.builder()
                .name("resend")
                .info(Constants.CHAT_REGISTRATION_STATE)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::contextHandler)
                .build();
    }

    public Ability changeMail() {
        return Ability.builder()
                .name("changemail")
                .info(Constants.CHAT_REGISTRATION_STATE)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::contextHandler)
                .build();
    }


    private void contextHandler(MessageContext messageContext) {
        UserEntity user = userService.findByChatId(String.valueOf(messageContext.chatId()));
        UserState registered = userStateRepository.findByName("registered");

        if (user != null) {
            //user already has sent email
            UserState userState = user.getStateId();
            if (userState.equals(registered)) {
                silent.send("You're already registred, want to change mail? - /changemail '{newMail}'", messageContext.chatId());
            } else {

                String stateName = userState.getName().toLowerCase();
                Update update = messageContext.update();

                if (stateName.equals("email_verification") && messageContext.update() != null && messageContext.update().hasMessage()) {
                    Message message = update.getMessage();
                    if (message.hasText() && message.getText().contains("/code") && !message.getText().substring(4).replaceAll(" ", "").isEmpty()) {
                        //initialise emailConfirmation
                        EmailConfirmationEntity byEmail = emailConfirmationEntityRepository.findByEmail(user.getEmail());
                        if (byEmail == null) {
                            String code = "Code:" + UUID.randomUUID();
                            EmailConfirmationEntity emailConfirmationEntity = new EmailConfirmationEntity();
                            emailConfirmationEntity.setIsConfirmed(false);
                            emailConfirmationEntity.setEmail(user.getEmail());
                            emailConfirmationEntity.setCode(code);
                            emailConfirmationEntityRepository.save(emailConfirmationEntity);
                            emailService.sendEmail(user.getEmail(), "Confirmation code", code);
                            silent.send("Email verification code was sent", messageContext.chatId());
                        } else {
                            //validate code process
                            String userSentCode = message.getText().substring(5).replaceAll(" ", "");
                            String toConfirm = byEmail.getCode();
                            if (toConfirm.equals(userSentCode)) {

                                user.setStateId(registered);
                                byEmail.setIsConfirmed(true);

                                userService.update(user);
                                emailConfirmationEntityRepository.saveAndFlush(byEmail);

                                silent.send("Successfully registered", messageContext.chatId());
                            } else {
                                silent.send("Wrong code,if you forgot code, try to /resend", messageContext.chatId());
                            }
                        }
                    } else if (message.hasText() && message.getText().contains("/resend")) {
                        EmailConfirmationEntity byEmail = emailConfirmationEntityRepository.findByEmail(user.getEmail());
                        if (byEmail != null && !byEmail.getIsConfirmed()) {
                            try {
                                emailService.sendEmail(byEmail.getEmail(), "Confirmation code", byEmail.getCode());
                            } catch (Exception e) {
                                silent.send("Catch exception while trying to sent verification code", messageContext.chatId());
                                e.printStackTrace();
                            }
                        } else {
                            silent.send("Smth broke, try to retry registration", messageContext.chatId());
                        }
                    } else {
                        silent.send("Verify your email to continue :) /code '{code}', to resend code /resend ", messageContext.chatId());
                    }
                } else if (stateName.equals("registered") && messageContext.update() != null && messageContext.update().hasMessage()) { //here's will be all processing with registred user
                    Message message = update.getMessage();

                    if (message.getText().contains("/changemail") && !message.getText().substring(11).replaceAll(" ", "").isEmpty()) {
                        String newMail = message.getText().substring(11).replaceAll(" ", "");
                        if (newMail.matches(MAIL_REGEX)) {

                            String pastMail = user.getEmail();
                            EmailConfirmationEntity emailConfirmation = emailConfirmationEntityRepository.findByEmail(pastMail);
                            emailConfirmation.setEmail(newMail);

                            try {
                                emailService.sendEmail(pastMail, "Email changed", "Mail changed from " + pastMail + " to " + newMail);
                                emailService.sendEmail(newMail, "This is new email", "Mail changed from " + pastMail + " to " + newMail);
                            } catch (Exception e) {
                                silent.send("Catch exception while trying to change email", messageContext.chatId());
                                e.printStackTrace();
                            }

                            user.setEmail(newMail);

                            userService.update(user);
                            emailConfirmationEntityRepository.saveAndFlush(emailConfirmation);
                            silent.send("Mail changed", messageContext.chatId());
                        } else {
                            silent.send("Not valid email address", messageContext.chatId());
                        }
                    } else if (message.getText().contains("/changemail") && message.getText().substring(11).replaceAll(" ", "").isEmpty()) {
                        silent.send("Send with new email: /changemail new@mail.com", messageContext.chatId());
                    } else {
                        silent.send("You're already registered", messageContext.chatId());
                    }
                }
            }
        } else { //user first time sent a message to bot
            if (messageContext.update() != null && messageContext.update().hasMessage()) {
                Update update = messageContext.update();
                Message message = update.getMessage();

                if (message != null && message.hasText() && message.getText().substring(4).replaceAll(" ", "").matches(MAIL_REGEX)) {
                    String code = "Code: " + UUID.randomUUID();
                    String email = message.getText().substring(4).replaceAll(" ", "");

                    UserEntity newUser = new UserEntity();
                    newUser.setStateId(userStateRepository.findByName("email_verification"));
                    newUser.setEmail(email);
                    newUser.setChatId(messageContext.chatId().toString());

                    EmailConfirmationEntity byEmail = emailConfirmationEntityRepository.findByEmail(email);
                    if (byEmail != null) {
                        silent.send("Code has been sent, to confirm /code '{code}'", messageContext.chatId());
                    } else {
                        try {
                            EmailConfirmationEntity newEmailConfirmation = new EmailConfirmationEntity();
                            newEmailConfirmation.setIsConfirmed(false);
                            newEmailConfirmation.setEmail(email);
                            newEmailConfirmation.setCode(code.substring(6));

                            emailService.sendEmail(email, "Confirmation code", code);
                            userService.save(newUser);

                            emailConfirmationEntityRepository.saveAndFlush(newEmailConfirmation);
                            silent.send("Code has been sent, to confirm /code '{code}'", messageContext.chatId());
                        } catch (Exception e) {
                            silent.send("Catch exception while trying to sent verification code", messageContext.chatId());
                            e.printStackTrace();
                        }
                    }

                } else if (message != null && message.hasText() && message.getText().substring(4).replaceAll(" ", "").isEmpty()) {
                    silent.send("Send email with email", messageContext.chatId());
                } else if (message != null && message.hasText() && !message.getText().substring(4).replaceAll(" ", "").matches(MAIL_REGEX)) {
                    silent.send("Not valid email address", messageContext.chatId());
                }

            }

        }

    }

    @Override
    public long creatorId() {
        return 228L;
    }
}

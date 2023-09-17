package com.example.producersvc.service.bot;

import com.example.producersvc.model.CurrencyEntity;
import com.example.producersvc.model.EmailConfirmationEntity;
import com.example.producersvc.model.UserEntity;
import com.example.producersvc.model.UserState;
import com.example.producersvc.repository.CurrencyEntityRepository;
import com.example.producersvc.repository.EmailConfirmationEntityRepository;
import com.example.producersvc.repository.UserLCurrencyRepository;
import com.example.producersvc.repository.UserStateRepository;
import com.example.producersvc.service.mail.EmailService;
import com.example.producersvc.service.user.UserService;
import com.example.producersvc.web.dto.CurrencyDTO;
import com.example.producersvc.web.dto.KafkaMessageFromProducerDTO;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static java.time.LocalTime.now;
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
    @Autowired
    private CurrencyEntityRepository currencyEntityRepository;
    @Autowired
    private UserLCurrencyRepository userLCurrencyRepository;
    @Autowired
    private EntityManager entityManager;

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
        return Ability.builder().name("start").info(Constants.START_DESCRIPTION)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Welcome to bot, need to registration to continue /reg to get more information /info", ctx.chatId())).build();
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

    public Ability info() {
        return Ability.builder()
                .name("info")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::infoHandler)
                .build();
    }

    public Ability sub() {
        return Ability.builder()
                .name("sub")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::subHandler)
                .build();
    }

    public Ability unSub() {
        return Ability.builder()
                .name("unsub")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::unSubHandler)
                .build();
    }

    public Ability onNotification() {
        return Ability.builder()
                .name("on")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::onNotification)
                .build();
    }

    public Ability offNotification() {
        return Ability.builder()
                .name("off")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::offNotification)
                .build();
    }

    public Ability unReg() {
        return Ability.builder()
                .name("unreg")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::unReg)
                .build();
    }

    @Transactional
    public void unReg(MessageContext messageContext) {
        UserEntity user = userService.findByChatId(String.valueOf(messageContext.chatId()));
        if (user != null) {
            userService.delete(user.getId());
            emailConfirmationEntityRepository.delete(emailConfirmationEntityRepository.findByEmail(user.getEmail()));
            userLCurrencyRepository.deleteByUser(user);
            silent.send("You're acc was deleted, need to registrate again", messageContext.chatId());
        }
    }

    @Transactional
    public void onNotification(MessageContext messageContext) {
        UserEntity user = userService.findByChatId(String.valueOf(messageContext.chatId()));
        UserState registered = userStateRepository.findByName("registered");

        if (user == null || !user.getStateId().getId().equals(registered.getId())) {
            silent.send("Need to register /reg {mail}", messageContext.chatId());
            return;
        }
        Message messageCon = messageContext.update().getMessage();
        String messageFromCon = messageCon.getText().substring(3).replaceAll(" ", "");

        if (messageFromCon.isEmpty()) {
            silent.send("What kinda notifications you want to turn on email or chat ? /on {mail/chat}", messageContext.chatId());
            return;
        }
        if (messageFromCon.equals("mail")) {
            user.setSubMail(true);
            userService.update(user);
            silent.send("Notification to email: " + user.getEmail() + " are turned on", messageContext.chatId());
        } else if (messageFromCon.equals("chat")) {
            user.setSubChat(true);
            userService.update(user);
            silent.send("Inchat notifications turned on", messageContext.chatId());
        } else {
            silent.send("Unexpected notification type: " + messageFromCon, messageContext.chatId());
        }

    }

    @Transactional
    public void offNotification(MessageContext messageContext) {
        UserEntity user = userService.findByChatId(String.valueOf(messageContext.chatId()));
        UserState registered = userStateRepository.findByName("registered");

        if (user == null || !user.getStateId().getId().equals(registered.getId())) {
            silent.send("Need to register /reg {mail}", messageContext.chatId());
            return;
        }
        Message messageCon = messageContext.update().getMessage();
        String messageFromCon = messageCon.getText().substring(4).replaceAll(" ", "");

        if (messageFromCon.isEmpty()) {
            silent.send("What kinda notifications you want to turn off email or chat ? /off {mail/chat}", messageContext.chatId());
            return;
        }
        if (messageFromCon.equals("mail")) {
            user.setSubMail(false);
            userService.update(user);
            silent.send("Notification to email: " + user.getEmail() + " are turned off", messageContext.chatId());
        } else if (messageFromCon.equals("chat")) {
            user.setSubChat(false);
            userService.update(user);
            silent.send("Inchat notifications turned off", messageContext.chatId());
        } else {
            silent.send("Unexpected notification type: " + messageFromCon, messageContext.chatId());
        }
    }

    @Transactional
    public void unSubHandler(MessageContext messageContext) {
        UserEntity user = userService.findByChatId(String.valueOf(messageContext.chatId()));
        UserState registered = userStateRepository.findByName("registered");

        if (user == null || !user.getStateId().getId().equals(registered.getId())) {
            silent.send("Need to register /reg {mail}", messageContext.chatId());
            return;
        }
        user = entityManager.find(UserEntity.class, user.getId());

        Message messageCon = messageContext.update().getMessage();
        String messageFromCon = messageCon.getText().substring(6).replaceAll(" ", "");

        if (messageFromCon.isEmpty()) {
            silent.send("Need to provide currency", messageContext.chatId());
            return;
        }

        CurrencyEntity currency = currencyEntityRepository.findByName(messageFromCon);

        if (currency == null) {
            silent.send("Wrong currency name", messageContext.chatId());
            return;
        }
        currency = entityManager.find(CurrencyEntity.class, currency.getId());

        Set<CurrencyEntity> userCurrencyEntities = user.getCurrencyEntities();
        if (userCurrencyEntities != null && !userCurrencyEntities.isEmpty()) {
            for (CurrencyEntity userCurrencyEntity : userCurrencyEntities) {
                if (userCurrencyEntity.getId().equals(currency.getId())) {
                    userLCurrencyRepository.delete(userLCurrencyRepository.findByUserAndCurrency(user, currency));
                }
            }
        }
        silent.send("Unsubscribed from " + currency.getName(), messageContext.chatId());

    }

    @Transactional
    public void subHandler(MessageContext messageContext) {
        UserEntity user = userService.findByChatId(String.valueOf(messageContext.chatId()));
        UserState registered = userStateRepository.findByName("registered");

        if (user == null || !user.getStateId().getId().equals(registered.getId())) {
            silent.send("Need to register /reg {mail}", messageContext.chatId());
            return;
        }

        Message messageCon = messageContext.update().getMessage();
        String messageFromCon = messageCon.getText().substring(4).replaceAll(" ", "");

        if (messageFromCon.isEmpty()) {
            silent.send("Need to provide currency", messageContext.chatId());
            return;
        }

        CurrencyEntity currency = currencyEntityRepository.findByName(messageFromCon);

        if (currency == null) {
            silent.send("Wrong currency name", messageContext.chatId());
            return;
        }

        Set<CurrencyEntity> userCurrencyEntities = user.getCurrencyEntities();
        if (userCurrencyEntities != null) {
            if (!userCurrencyEntities.contains(currency))
                userCurrencyEntities.add(currency);
        } else {
            user.setCurrencyEntities(new HashSet<>(Collections.singleton(currency)));
        }
        userService.update(user);
        silent.send("Subscribed to " + currency.getName(), messageContext.chatId());
    }

    private void infoHandler(MessageContext messageContext) {
        String info = """
                List of commands:
                                
                /reg {mail} - registration
                /unreg - deletes user info, need to /reg again
                /code {code} - code verification
                /resend - resend verification code
                /changemail {mail} - changing mail without confirmation, i'm to lazy to create algo
                /off mail/chat - unsubscribe from notifications on mail/chat
                /on mail/chat - subscribe to mail/chat notifications(by default when you finish registration you already subscribed c:)
                /sub {currency} - currency: EUR, USD, CNY  - bank key rate; subscribe for currency changes
                /unsub {currency} - currency: EUR, USD, CNY - bank key rate; unsubscribe for currency changes
                                
                This is pet bot for simple practice in message brokers,spring boot 3 and microservice. Whole source code you can find - https://github.com/ogbozoyan/Notification-App-Spring
                """;
        silent.send(info, messageContext.chatId());
    }


    @Transactional
    public void contextHandler(MessageContext messageContext) {
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

    @Transactional
    public void sendToUsers(List<KafkaMessageFromProducerDTO> userList) {
        if (userList != null && !userList.isEmpty()) {
            for (KafkaMessageFromProducerDTO dto : userList) {

                UserEntity user = userService.findById(dto.getUserId());
                if (user == null) {
                    throw new RuntimeException("Can't find user with id: " + dto.getUserId());
                }

                CurrencyDTO currencyDTO = dto.getCurrencyDTO();
                StringBuilder message = new StringBuilder("""
                        Prices:
                                                
                        """);
                Set<CurrencyEntity> userCurrencyEntities = user.getCurrencyEntities();

                if (!userCurrencyEntities.isEmpty()) {
                    for (CurrencyEntity userCurrencyEntity : userCurrencyEntities) {
                        if (userCurrencyEntity.getName().equalsIgnoreCase("usd")) {
                            message.append("USD: ").append(currencyDTO.getUsd()).append("\n");
                        } else if (userCurrencyEntity.getName().equalsIgnoreCase("cny")) {
                            message.append("CNY: ").append(currencyDTO.getCny()).append("\n");
                        } else if (userCurrencyEntity.getName().equalsIgnoreCase("eur")) {
                            message.append("EUR: ").append(currencyDTO.getEur()).append("\n");
                        }
                    }
                } else { //if a user isn't subscribed to any currency
                    log.info("User with id {} and email {} arent subscribed to any currency",user.getId(),user.getEmail());
                    return;
                }

                Long chatId = Long.valueOf(user.getChatId());
                String userEmail = user.getEmail();

                if (user.getSubChat()) { //if user subscribed to receive notifications in chat
                    if (sendToChatId(chatId, message.toString()).isPresent()) {
                        log.info("Notification to chat sent, chatId: {}", chatId);
                    } else {
                        log.info("Didn't sent message to chat with id: " + chatId);
                    }
                } else {
                    log.info("User not subscribed to chat notifications");
                }


                String subject = "Курс на " + now();
                if (user.getSubMail()) {//if user subscribed to receive notifications to mail
                    if (emailService.sendEmail(userEmail, subject, message.toString())) {
                        log.info("Email sent to: {} subject: {} with text: {}", userEmail, subject, message.toString());
                    } else {
                        log.info("Didn't sent message to mail {}", userEmail);
                    }
                } else {
                    log.info("User not subscribed to email notifications");
                }
            }
        }
    }


    private Optional<Message> sendToChatId(Long chatId, String message) {
        Optional<Message> send;
        try {
            send = silent.send(message, chatId);
            return send;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public long creatorId() {
        return 228L;
    }
}

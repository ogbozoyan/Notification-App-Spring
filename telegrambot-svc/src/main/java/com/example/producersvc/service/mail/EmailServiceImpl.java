package com.example.producersvc.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author ogbozoyan
 * @since 11.09.2023
 */
@Component
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    public String MAIL_USERNAME;

    @Override
    public boolean sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(MAIL_USERNAME);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            sender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

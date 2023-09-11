package com.example.producersvc.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author ogbozoyan
 * @since 11.09.2023
 */
@Component
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    public String MAIL_USERNAME;

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(MAIL_USERNAME);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            sender.send(message);
        } catch (Exception e) {
            throw e;
        }
    }
}

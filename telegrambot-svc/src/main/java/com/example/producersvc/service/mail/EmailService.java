package com.example.producersvc.service.mail;

/**
 * @author ogbozoyan
 * @since 11.09.2023
 */
public interface EmailService {
        void sendEmail(String to,
                       String subject,
                       String text);
}

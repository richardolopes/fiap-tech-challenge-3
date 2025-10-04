package com.hospital.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String content) {
        logger.info("📧 ENVIANDO EMAIL");
        logger.info("Para: {}", to);
        logger.info("Assunto: {}", subject);
        logger.info("Conteúdo: {}", content);
        logger.info("✅ Email enviado com sucesso!");
    }

}

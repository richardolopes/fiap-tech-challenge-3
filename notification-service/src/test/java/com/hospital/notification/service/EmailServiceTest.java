package com.hospital.notification.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailService Tests")
class EmailServiceTest {

    private EmailService emailService;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
        
        logger = (Logger) LoggerFactory.getLogger(EmailService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    @DisplayName("Should send email and log details")
    void shouldSendEmailAndLogDetails() {
        String to = "patient@test.com";
        String subject = "Test Subject";
        String content = "Test Content";

        emailService.sendEmail(to, subject, content);

        var logsList = listAppender.list;
        assertEquals(5, logsList.size());
        
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertEquals("📧 ENVIANDO EMAIL", logsList.get(0).getMessage());
        
        assertEquals(Level.INFO, logsList.get(1).getLevel());
        assertEquals("Para: {}", logsList.get(1).getMessage());
        assertEquals(to, logsList.get(1).getArgumentArray()[0]);
        
        assertEquals(Level.INFO, logsList.get(2).getLevel());
        assertEquals("Assunto: {}", logsList.get(2).getMessage());
        assertEquals(subject, logsList.get(2).getArgumentArray()[0]);
        
        assertEquals(Level.INFO, logsList.get(3).getLevel());
        assertEquals("Conteúdo: {}", logsList.get(3).getMessage());
        assertEquals(content, logsList.get(3).getArgumentArray()[0]);
        
        assertEquals(Level.INFO, logsList.get(4).getLevel());
        assertEquals("✅ Email enviado com sucesso!", logsList.get(4).getMessage());
    }

    @Test
    @DisplayName("Should send email with null parameters")
    void shouldSendEmailWithNullParameters() {
        assertDoesNotThrow(() -> emailService.sendEmail(null, null, null));
        
        var logsList = listAppender.list;
        assertEquals(5, logsList.size());
        assertNull(logsList.get(1).getArgumentArray()[0]); 
        assertNull(logsList.get(2).getArgumentArray()[0]); 
        assertNull(logsList.get(3).getArgumentArray()[0]); 
    }

    @Test
    @DisplayName("Should send email with empty parameters")
    void shouldSendEmailWithEmptyParameters() {
        String to = "";
        String subject = "";
        String content = "";

        emailService.sendEmail(to, subject, content);

        var logsList = listAppender.list;
        assertEquals(5, logsList.size());
        assertEquals("", logsList.get(1).getArgumentArray()[0]);
        assertEquals("", logsList.get(2).getArgumentArray()[0]);
        assertEquals("", logsList.get(3).getArgumentArray()[0]);
    }

    @Test
    @DisplayName("Should send email with long content")
    void shouldSendEmailWithLongContent() {
        String to = "test@example.com";
        String subject = "Long Content Test";
        String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(100);

        emailService.sendEmail(to, subject, content);

        var logsList = listAppender.list;
        assertEquals(5, logsList.size());
        assertEquals(content, logsList.get(3).getArgumentArray()[0]);
    }

    @Test
    @DisplayName("Should send email with special characters")
    void shouldSendEmailWithSpecialCharacters() {
        String to = "test@example.com";
        String subject = "Subject with special chars: àáâãäç!@#$%";
        String content = "Content with emojis: 📧 ✅ 🎉 and accents: àáâãäç";

        emailService.sendEmail(to, subject, content);

        var logsList = listAppender.list;
        assertEquals(5, logsList.size());
        assertEquals(subject, logsList.get(2).getArgumentArray()[0]);
        assertEquals(content, logsList.get(3).getArgumentArray()[0]);
    }

    @Test
    @DisplayName("Should handle multiple email sends")
    void shouldHandleMultipleEmailSends() {
        emailService.sendEmail("user1@test.com", "Subject 1", "Content 1");
        emailService.sendEmail("user2@test.com", "Subject 2", "Content 2");
        emailService.sendEmail("user3@test.com", "Subject 3", "Content 3");

        var logsList = listAppender.list;
        assertEquals(15, logsList.size());
    }
}

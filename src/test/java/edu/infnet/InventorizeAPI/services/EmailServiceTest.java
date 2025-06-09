package edu.infnet.InventorizeAPI.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void shouldSendEmailWithValidParameters() {
        var to = "test@example.com";
        var body = "This is a test email.";
        var messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(to, body);

        verify(mailSender).send(messageCaptor.capture());
        var sentMessage = messageCaptor.getValue();

        assertNotNull(sentMessage.getTo());
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("Alerta de estoque baixo", sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @Test
    public void shouldCreateEmailBodyWithValidParameters() {
        var inventoryName = "Test Inventory";
        var itemName = "Test Item";
        var quantity = 5;

        var expectedBody = String.format("O inventário '%s' está com o item '%s' com quantidade baixa: %d unidades.",
                inventoryName, itemName, quantity);

        var actualBody = emailService.createEmailBody(inventoryName, itemName, quantity);

        assertEquals(expectedBody, actualBody);
    }

    @Test
    void shouldThrowExceptionWhenEmailSendingFails() {
        doThrow(new MailSendException("Erro ao enviar")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailSendException.class, () -> {
            emailService.sendEmail("example@email.com", "Subject");
        });
    }
}

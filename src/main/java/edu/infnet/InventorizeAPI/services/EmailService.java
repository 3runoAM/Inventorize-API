package edu.infnet.InventorizeAPI.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Alerta de estoque baixo");
        message.setText(body);
    }

    public String createEmailBody(String inventoryName, String itemName, int quantity) {
        return String.format("O inventário '%s' está com o item '%s' com quantidade baixa: %d unidades.",
                inventoryName, itemName, quantity);
    }
}

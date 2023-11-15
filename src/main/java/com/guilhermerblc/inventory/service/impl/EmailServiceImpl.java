package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.models.Product;
import com.guilhermerblc.inventory.repository.ConfigurationRepository;
import com.guilhermerblc.inventory.service.ConfigurationService;
import com.guilhermerblc.inventory.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${mail.system-email}")
    private String sender;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Async("processExecutor")
    private void sendSimpleMail(String tile, String body) {
        Optional<Configuration> alertEmail = configurationRepository.getByName("ALERT_EMAIL");

        if (alertEmail.isEmpty()) {
            throw new RuntimeException("The alert e-mail is not configured!");
        }

        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(alertEmail.get().getData());
            mailMessage.setSubject(tile);
            mailMessage.setText(body);

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.out.println("Error while sending email.");
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Override
    public void sendLowInventoryEmailAlert(Product product, Long currentInventory) {
        String body = "Alerta de estoque baixo para o produto: " + product.getDescription() + "\n\n";
        body += "O estoque mínimo esperado é " + product.getInventoryMinimum() + " e o atual é " + currentInventory + ".\n";
        body += "\nEste é um e-mail automático do G Inventory System.";
        sendSimpleMail("Alerta de estoque baixo! (" + product.getDescription() + ")", body);
    }

    @Override
    public void sendHighInventoryEmailAlert(Product product, Long currentInventory) {
        String body = "Alerta de estoque cheio para o produto: " + product.getDescription() + "\n\n";
        body += "O estoque máximo esperado é " + product.getInventoryMaximum() + " e o atual é " + currentInventory + ".\n";
        body += "\nEste é um e-mail automático do G Inventory System.";
        sendSimpleMail("Alerta de estoque cheio! (" + product.getDescription() + ")", body);
    }
}

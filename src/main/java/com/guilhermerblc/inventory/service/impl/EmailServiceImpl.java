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

@EnableAsync
@Service
public class EmailServiceImpl implements EmailService {

    /*
     * WARNING: The notification e-mail doesn't work in tests because it's asynchronous and the server finishes before
     * the email is sent. But it can be done if I put something to wait the end of all tasks on the tests like in this
     * link: https://stackoverflow.com/questions/42438862/junit-testing-a-spring-async-void-service-method#:~:text=0,the%20above%20solutions%3A
     * */

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${mail.system-email}")
    private String sender;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Async
    @Override
    public void sendSimpleMail(String tile, String body) {
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

    @Async
    @Override
    public void sendLowInventoryEmailAlert(Product product, Long currentInventory) {
        String body = "Alerta de estoque baixo para o produto: " + product.getDescription() + "\n\n";
        body += "O estoque mínimo esperado é " + product.getInventoryMinimum() + " e o atual é " + currentInventory + ".\n";
        body += "\nEste é um e-mail automático do G Inventory System.";
        sendSimpleMail("Alerta de estoque baixo! (" + product.getDescription() + ")", body);
    }

    @Async
    @Override
    public void sendHighInventoryEmailAlert(Product product, Long currentInventory) {
        String body = "Alerta de estoque cheio para o produto: " + product.getDescription() + "\n\n";
        body += "O estoque máximo esperado é " + product.getInventoryMaximum() + " e o atual é " + currentInventory + ".\n";
        body += "\nEste é um e-mail automático do G Inventory System.";
        sendSimpleMail("Alerta de estoque cheio! (" + product.getDescription() + ")", body);
    }
}

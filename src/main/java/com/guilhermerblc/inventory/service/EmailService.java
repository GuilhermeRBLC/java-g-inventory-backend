package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.Product;

public interface EmailService {

    void sendSimpleMail(String tile, String body);

    void sendLowInventoryEmailAlert(Product product, Long currentInventory);

    void sendHighInventoryEmailAlert(Product product, Long currentInventory);

}

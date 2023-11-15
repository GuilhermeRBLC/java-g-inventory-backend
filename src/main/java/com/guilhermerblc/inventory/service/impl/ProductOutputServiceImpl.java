package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.exceptions.IdentificationNotEqualsException;
import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductInputRepository;
import com.guilhermerblc.inventory.repository.ProductOutputRepository;
import com.guilhermerblc.inventory.service.EmailService;
import com.guilhermerblc.inventory.service.ProductOutputService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ProductOutputServiceImpl implements ProductOutputService {

    private final ProductOutputRepository repository;

    private final ProductInputRepository inputRepository;

    private final EmailService emailService;

    private void sendInventoryAlert(ProductOutput productOutput) {
        List<ProductInput> productInputs = inputRepository.findByProductId(productOutput.getProduct().getId());
        Long currentInputInventory = productInputs.stream().reduce(0L, (s, obj) -> s + obj.getQuantity(), Long::sum);

        List<ProductOutput> productOutputs = repository.findByProductId(productOutput.getProduct().getId());
        Long currentOutputInventory = productOutputs.stream().reduce(0L, (s, obj) -> s + obj.getQuantity(), Long::sum);

        long currentInventory = currentInputInventory - currentOutputInventory;

        if (currentInventory > productOutput.getProduct().getInventoryMaximum()) {
            emailService.sendHighInventoryEmailAlert(productOutput.getProduct(), currentInventory);
        } else if (currentInventory < productOutput.getProduct().getInventoryMinimum()) {
            emailService.sendLowInventoryEmailAlert(productOutput.getProduct(), currentInventory);
        }
    }

    @Override
    public List<ProductOutput> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductOutput> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Override
    public ProductOutput findById(Long id) {
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public ProductOutput crate(ProductOutput entity) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        entity.setUser(authenticatedUser);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);

        ProductOutput productOutputSaved = repository.save(entity);
        sendInventoryAlert(productOutputSaved);

        return productOutputSaved;
    }

    @Override
    public ProductOutput update(Long id, ProductOutput entity) {
        ProductOutput productOutput = findById(id);

        if(!productOutput.getId().equals(entity.getId())) {
            throw new IdentificationNotEqualsException("Update IDs must be the same.");
        }

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        productOutput.setProduct(entity.getProduct());
        productOutput.setBarcode(entity.getBarcode());
        productOutput.setBuyer(entity.getBuyer());
        productOutput.setSaleValue(entity.getSaleValue());
        productOutput.setSaleDate(entity.getSaleDate());
        productOutput.setQuantity(entity.getQuantity());
        productOutput.setObservations(entity.getObservations());
        productOutput.setUser(authenticatedUser);
        productOutput.setModified(LocalDateTime.now());

        ProductOutput productOutputSaved = repository.save(productOutput);
        sendInventoryAlert(productOutputSaved);

        return productOutputSaved;
    }

    @Override
    public void delete(Long id) {

        ProductOutput productOutput = repository.findById(id).orElseThrow();
        sendInventoryAlert(productOutput);

        repository.deleteById(id);
    }

}

package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.exceptions.IdentificationNotEqualsException;
import com.guilhermerblc.inventory.models.ProductInput;
import com.guilhermerblc.inventory.models.ProductOutput;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ProductInputRepository;
import com.guilhermerblc.inventory.repository.ProductOutputRepository;
import com.guilhermerblc.inventory.service.EmailService;
import com.guilhermerblc.inventory.service.ProductInputService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@EnableAsync
@Service
@AllArgsConstructor
public class ProductInputServiceImpl implements ProductInputService {

    private final ProductInputRepository repository;

    private final ProductOutputRepository outputRepository;

    private final EmailService emailService;

    private void sendInventoryAlert(ProductInput productInput) {
        List<ProductInput> productInputs = repository.findByProductId(productInput.getProduct().getId());
        Long currentInputInventory = productInputs.stream().reduce(0L, (s, obj) -> s + obj.getQuantity(), Long::sum);

        List<ProductOutput> productOutputs = outputRepository.findByProductId(productInput.getProduct().getId());
        Long currentOutputInventory = productOutputs.stream().reduce(0L, (s, obj) -> s + obj.getQuantity(), Long::sum);

        long currentInventory = currentInputInventory - currentOutputInventory;

        if (currentInventory > productInput.getProduct().getInventoryMaximum()) {
            emailService.sendHighInventoryEmailAlert(productInput.getProduct(), currentInventory);
        } else if (currentInventory < productInput.getProduct().getInventoryMinimum()) {
            emailService.sendLowInventoryEmailAlert(productInput.getProduct(), currentInventory);
        }
    }

    @Override
    public List<ProductInput> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductInput> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Override
    public ProductInput findById(Long id) {
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public ProductInput crate(ProductInput entity) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        entity.setUser(authenticatedUser);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);

        ProductInput productInputSaved = repository.save(entity);
        sendInventoryAlert(productInputSaved);

        return productInputSaved;
    }

    @Override
    public ProductInput update(Long id, ProductInput entity) {
        ProductInput productInput = findById(id);

        if(!productInput.getId().equals(entity.getId())) {
            throw new IdentificationNotEqualsException("Update IDs must be the same.");
        }

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        productInput.setProduct(entity.getProduct());
        productInput.setBarcode(entity.getBarcode());
        productInput.setSupplier(entity.getSupplier());
        productInput.setPurchaseValue(entity.getPurchaseValue());
        productInput.setPurchaseDate(entity.getPurchaseDate());
        productInput.setQuantity(entity.getQuantity());
        productInput.setObservations(entity.getObservations());
        productInput.setUser(authenticatedUser);
        productInput.setModified(LocalDateTime.now());

        ProductInput productInputSaved = repository.save(productInput);
        sendInventoryAlert(productInputSaved);

        return productInputSaved;
    }

    @Override
    public void delete(Long id) {

        ProductInput productInput = repository.findById(id).orElseThrow();
        sendInventoryAlert(productInput);

        repository.deleteById(id);
    }
}

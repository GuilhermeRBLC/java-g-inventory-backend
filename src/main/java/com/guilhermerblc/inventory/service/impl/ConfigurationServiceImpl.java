package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.exceptions.IdentificationNotEqualsException;
import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.repository.ConfigurationRepository;
import com.guilhermerblc.inventory.service.ConfigurationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<Configuration> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Configuration findById(Long id) {
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    @Override
    public Configuration update(Long id, Configuration entity) {
        Configuration configuration = findById(id);

        if(!configuration.getId().equals(entity.getId())) {
            throw new IdentificationNotEqualsException("Update IDs must be the same.");
        }

        configuration.setData(entity.getData());
        configuration.setModified(LocalDateTime.now());

        return repository.save(configuration);
    }

}

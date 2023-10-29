package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.repository.ConfigurationRepository;
import com.guilhermerblc.inventory.service.ConfigurationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationRepository repository;

    @Override
    public List<Configuration> findAll() {
        return repository.findAll();
    }

    @Override
    public Configuration findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public Configuration update(Long id, Configuration entity) {
        Configuration configuration = findById(id);

        configuration.setData(entity.getData());
        configuration.setModified(LocalDateTime.now());

        return repository.save(configuration);
    }

}

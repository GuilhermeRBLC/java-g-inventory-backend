package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.Configuration;

import java.util.List;

public interface ConfigurationService {

    List<Configuration> findAll();

    Configuration findById(Long id);

    Configuration update(Long id, Configuration entity);

}

package com.guilhermerblc.inventory.repository;

import com.guilhermerblc.inventory.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

}

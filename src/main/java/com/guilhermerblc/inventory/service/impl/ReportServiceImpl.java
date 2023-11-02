package com.guilhermerblc.inventory.service.impl;

import com.guilhermerblc.inventory.models.Report;
import com.guilhermerblc.inventory.repository.ReportRepository;
import com.guilhermerblc.inventory.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repository;

    @Override
    public List<Report> findAll() {
        return repository.findAll();
    }

    @Override
    public Report findById(Long id) {
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Report crate(Report entity) {
        entity.setCreated(LocalDateTime.now());
        entity.setModified(null);
        return repository.save(entity);
    }

    @Override
    public Report update(Long id, Report entity) {
        Report report = findById(id);

        if(!report.getId().equals(entity.getId())) {
            throw new RuntimeException("Update IDs must be the same.");
        }

        report.setDescription(entity.getDescription());
        report.setFilters(entity.getFilters());
        report.setModified(LocalDateTime.now());

        return repository.save(report);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

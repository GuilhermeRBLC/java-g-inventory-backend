package com.guilhermerblc.inventory.service;

import com.guilhermerblc.inventory.models.Report;

import java.util.List;

public interface ReportService {

    List<Report> findAll();

    Report findById(Long id);

    Report crate(Report entity);

    Report update(Long id, Report entity);

    void delete(Long id);

}

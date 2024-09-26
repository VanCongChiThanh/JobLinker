package com.joblinker.controller;

import com.joblinker.domain.Company;
import com.joblinker.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company)
    {
        com.joblinker.domain.Company newCompany = companyService.saveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

}

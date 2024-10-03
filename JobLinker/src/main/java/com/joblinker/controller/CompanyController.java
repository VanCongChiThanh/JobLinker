package com.joblinker.controller;

import com.joblinker.domain.Company;
import com.joblinker.domain.dto.ResultPaginationDTO;
import com.joblinker.domain.dto.SearchCriteria;
import com.joblinker.repository.GenericSpecification;
import com.joblinker.service.CompanyService;
import com.joblinker.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
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
    @PutMapping("/companies/{companyId}")
    public ResponseEntity<Company> updateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody Company reqCompany){

        Company updatedCompany = companyService.updateCompany(companyId, reqCompany);

        return updatedCompany != null
                ? ResponseEntity.ok(updatedCompany)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/companies")
    @ApiMessage("fetch companys")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            Pageable pageable,
            @RequestParam(value = "key", required = false) Optional<String> keyOptional,
            @RequestParam(value = "operation", required = false) Optional<String> operationOptional,
            @RequestParam(value = "value", required = false) Optional<String> valueOptional
    ) {

        SearchCriteria criteria = buildSearchCriteria(keyOptional, operationOptional, valueOptional);
        GenericSpecification<Company> spec = new GenericSpecification<>(criteria);

        return ResponseEntity.ok(companyService.getCompanyList(pageable, spec));
    }
    private SearchCriteria buildSearchCriteria(Optional<String> key, Optional<String> operation, Optional<String> value) {
        if (key.isPresent() && operation.isPresent() && value.isPresent()) {
            return new SearchCriteria(key.get(), operation.get(), value.get());
        }
        return null;
    }

    @GetMapping("/companies/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyService.getCompanyById(companyId));
    }
    @DeleteMapping("/companies/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
         this.companyService.deleteCompany(companyId);

        return ResponseEntity.ok(null);
    }

}

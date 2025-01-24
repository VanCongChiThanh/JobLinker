package com.joblinker.controller;

import com.joblinker.domain.Company;
import com.joblinker.domain.User;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.domain.dto.SearchCriteria;
import com.joblinker.service.CompanyService;
import com.joblinker.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody Company reqCompany) {

        Company updatedCompany = this.companyService.updateCompany(id, reqCompany);

        return updatedCompany != null
                ? ResponseEntity.ok(updatedCompany)
                : ResponseEntity.notFound().build();
    }


//    @GetMapping("/companies")
//    @ApiMessage("fetch companies")
//    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
//            Pageable pageable,
//            @RequestParam(value = "key", required = false) String key,
//            @RequestParam(value = "operation", required = false) String operation,
//            @RequestParam(value = "value", required = false) String value
//    ) {
//
//        SearchCriteria criteria = null;
//        if (key != null && operation != null && value != null) {
//            criteria = new SearchCriteria(key, operation, value);
//        }
//
//        GenericSpecification<Company> spec = new GenericSpecification<>(criteria);
//
//        return ResponseEntity.ok(companyService.getCompanyList(pageable, spec));
//    }
     @GetMapping("/companies")
     @ApiMessage("Fetch companies")
     public ResponseEntity<ResultPaginationDTO> getCompanies(
        @Filter Specification<Company> spec,
        Pageable pageable) {
        return ResponseEntity.ok(this.companyService.handleGetCompany(spec, pageable));
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
    @GetMapping("/companies/top-companies")
    public List<Company> getTopCompanies() {
        return companyService.getTopCompaniesWithMostJobs(12);
    }
    @GetMapping("/companies/by-employer/{userId}")
    public ResponseEntity<Company> getCompanyByUserId(@PathVariable Long userId) {
        Company company = companyService.getCompanyByUserId(userId);
        return ResponseEntity.ok(company);
    }

}
package com.joblinker.controller;

import com.joblinker.domain.Company;
import com.joblinker.domain.dto.ResultPaginationDTO;
import com.joblinker.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
        @RequestParam("current") Optional<String> currentOptional ,
        @RequestParam("pageSize") Optional<String> pageSizeOptional
    )
    {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSize=pageSizeOptional.isPresent()?pageSizeOptional.get():"";
        Pageable pageable = PageRequest.of(Integer.parseInt(sCurrent)-1,Integer.parseInt(sPageSize));
        return ResponseEntity.ok(companyService.getCompanyList(pageable));
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

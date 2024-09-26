package com.joblinker.service;

import com.joblinker.domain.Company;
import com.joblinker.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }
    public List<Company> getCompanyList() {
        return companyRepository.findAll();
    }
}

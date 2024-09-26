package com.joblinker.service;

import com.joblinker.domain.Company;
import com.joblinker.domain.RestResponse;
import com.joblinker.domain.dto.Meta;
import com.joblinker.domain.dto.ResultPaginationDTO;
import com.joblinker.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResultPaginationDTO getCompanyList(Pageable pageable) {
        Page<Company> pageCompanies=companyRepository.findAll(pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta=new Meta();
        meta.setPage(pageCompanies.getNumber()+1);
        meta.setPageSize(pageCompanies.getSize());

        meta.setTotal(pageCompanies.getTotalElements());
        meta.setPages(pageCompanies.getTotalPages());

        result.setMeta(meta);
        result.setResult(pageCompanies.getContent());
        return result;
    }
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }
    public Company updateCompany(Long id, Company company) {
        Company existingCompany=companyRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Could not find company"));
        existingCompany.setName(company.getName());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setLogo(company.getLogo());
        existingCompany.setDescription(company.getDescription());
        return companyRepository.save(existingCompany);
    }
    public boolean deleteCompany(Long id) {
        if(!companyRepository.existsById(id)){
            throw new EntityNotFoundException("Company with ID:" + id + "does not exist");
        }
        try{
            companyRepository.deleteById(id);
            return true;
        }catch(Exception e){
            throw new RuntimeException("Failed to delete company with ID:" + id);
        }
    }

}

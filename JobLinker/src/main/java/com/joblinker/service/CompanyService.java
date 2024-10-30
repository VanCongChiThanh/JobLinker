package com.joblinker.service;

import com.joblinker.domain.Company;
import com.joblinker.domain.dto.ResultPaginationDTO;
import com.joblinker.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }
//    public ResultPaginationDTO getCompanyList(Pageable pageable, GenericSpecification<Company> spec) {
//        Page<Company> pageCompanies=companyRepository.findAll(spec,pageable);
//        ResultPaginationDTO result = new ResultPaginationDTO();
//        Meta meta=new Meta();
//        meta.setPage(pageCompanies.getNumber()+1);
//        meta.setPageSize(pageCompanies.getSize());
//
//        meta.setTotal(pageCompanies.getTotalElements());
//        meta.setPages(pageCompanies.getTotalPages());
//
//        result.setMeta(meta);
//        result.setResult(pageCompanies.getContent());
//        return result;
//    }
public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
    Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pCompany.getTotalPages());
    mt.setTotal(pCompany.getTotalElements());

    rs.setMeta(mt);
    rs.setResult(pCompany.getContent());
    return rs;
}

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }
    public Company updateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
        if(companyOptional.isPresent()) {
            Company existingCompany = companyOptional.get();
            existingCompany.setName(company.getName());
            existingCompany.setAddress(company.getAddress());
            existingCompany.setLogo(company.getLogo());
            existingCompany.setDescription(company.getDescription());
            return this.companyRepository.save(existingCompany);
        }
        return null;
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

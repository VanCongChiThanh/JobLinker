package com.joblinker.service;

import com.joblinker.domain.Company;
import com.joblinker.domain.Role;
import com.joblinker.domain.User;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.repository.CompanyRepository;
import com.joblinker.repository.UserRepository;
import com.joblinker.util.error.CustomException;
import com.joblinker.util.error.IdInvalidException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
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
        return companyRepository.findById(id).orElseThrow(()->new IdInvalidException("Company id not found"));
    }
    public Company updateCompany(Long id,Company company) {
        //use option to avoid nullpointexception
        Optional<Company> companyOptional = this.companyRepository.findById(id);
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
    public void deleteCompany(Long id) {
        Optional<Company> optionalCompany= this.companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            Company company= optionalCompany.get();
            List<User> users=this.userRepository.findByCompany(company);
            users.forEach(user -> user.setCompany(null));
        }
        this.companyRepository.deleteById(id);
    }
    public List<Company> getTopCompaniesWithMostJobs(int limit) {
        return companyRepository.findTopCompaniesByJobCount(PageRequest.of(0, limit));
    }
    public Company getCompanyByUserId(Long userId) {
        // Tìm user dựa trên userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User ID not found."));

        // Kiểm tra vai trò của user
        if (user.getRole() == null || !"EMPLOYER".equalsIgnoreCase(user.getRole().getName())) {
            throw new CustomException("User does not have permission to access this resource.");
        }

        // Trả về công ty liên kết với user
        Company company = user.getCompany();
        if (company == null) {
            throw new CustomException("No company associated with this user.");
        }

        return company;
    }

}
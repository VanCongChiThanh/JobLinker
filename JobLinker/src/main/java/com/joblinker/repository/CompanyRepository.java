package com.joblinker.repository;

import com.joblinker.domain.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> , JpaSpecificationExecutor<Company> {
    @Query("SELECT c FROM Company c LEFT JOIN c.jobs j GROUP BY c ORDER BY COUNT(j) DESC")
    List<Company> findTopCompaniesByJobCount(Pageable pageable);
}
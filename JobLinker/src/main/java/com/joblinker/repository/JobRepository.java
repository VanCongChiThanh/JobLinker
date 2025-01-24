package com.joblinker.repository;

import com.joblinker.domain.Company;
import com.joblinker.domain.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> , JpaSpecificationExecutor<Job> {
    @Query("SELECT j FROM Job j LEFT JOIN j.resumes r GROUP BY j ORDER BY COUNT(r) DESC")
    List<Job> findTopJobsByResumesCount(Pageable pageable);

    List<Job> findByCompanyId(Long id);
}
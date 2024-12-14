package com.joblinker.service;

import com.joblinker.domain.Company;
import com.joblinker.domain.Job;
import com.joblinker.domain.Skill;
import com.joblinker.domain.response.Job.ResCreateJobDTO;
import com.joblinker.domain.response.Job.ResUpdateJobDTO;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.repository.CompanyRepository;
import com.joblinker.repository.JobRepository;
import com.joblinker.repository.SkillRepository;
import com.joblinker.util.error.CustomException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepositor;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepositor, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepositor = skillRepositor;
        this.companyRepository = companyRepository;
    }
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new CustomException("Job with id = " + id + " not found"));
    }
    public ResCreateJobDTO createJob(Job job) {
        if(job.getSkills()!=null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepositor.findAllById(reqSkills);
            if (dbSkills.size() != reqSkills.size()) {
                throw new CustomException("Some skills were not found in the database.");
            }
            job.setSkills(dbSkills);
        }

        Job currentJob = this.jobRepository.save(job);
        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartTime());
        dto.setEndDate(currentJob.getEndTime());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if(currentJob.getSkills()!=null){
            List<String> skills = currentJob.getSkills().stream().map(item->item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }
    public ResUpdateJobDTO updateJob(Long id,Job job) {
        Job existingJob =this.jobRepository.findById(id)
                .orElseThrow(() -> new CustomException("Job with id = " + job.getId() + " not found"));

        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(Skill::getId)
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepositor.findAllById(reqSkills);
            if (dbSkills.size() != reqSkills.size()) {
                throw new CustomException("Some skills were not found in the database.");
            }
            existingJob.setSkills(dbSkills);
        }

        if (job.getCompany() != null) {
            Company company = this.companyRepository.findById(job.getCompany().getId())
                    .orElseThrow(() -> new CustomException("Company not found"));
            existingJob.setCompany(company);
        }
        existingJob.setName(job.getName());
        existingJob.setSalary(job.getSalary());
        existingJob.setDescription(job.getDescription());
        existingJob.setQuantity(job.getQuantity());
        existingJob.setLocation(job.getLocation());
        existingJob.setLevel(job.getLevel());
        existingJob.setStartTime(job.getStartTime());
        existingJob.setEndTime(job.getEndTime());
        existingJob.setActive(true);

        Job currentJob = this.jobRepository.save(existingJob);
        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartTime());
        dto.setEndDate(currentJob.getEndTime());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if(currentJob.getSkills()!=null){
            List<String> skills = currentJob.getSkills().stream().map(item->item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }
    public ResultPaginationDTO getAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageUser = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageUser.getContent());

        return rs;
    }
    public void delete(long id) {
        this.jobRepository.findById(id)
                .orElseThrow(() -> new CustomException("Job with id = " + id + " not found, could not delete"));
        this.jobRepository.deleteById(id);
    }
    public List<Job> getTopJobsWithMostResumes(int limit) {
        return jobRepository.findTopJobsByResumesCount(Pageable.ofSize(limit));
    }
}
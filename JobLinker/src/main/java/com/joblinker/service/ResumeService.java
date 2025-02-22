package com.joblinker.service;

import com.joblinker.domain.Job;
import com.joblinker.domain.Resume;
import com.joblinker.domain.User;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.domain.response.Resume.ResCreateResumeDTO;
import com.joblinker.domain.response.Resume.ResFetchResumeDTO;
import com.joblinker.domain.response.Resume.ResUpdateResumeDTO;
import com.joblinker.repository.JobRepository;
import com.joblinker.repository.ResumeRepository;
import com.joblinker.repository.UserRepository;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.error.CustomException;
import com.joblinker.util.error.IdInvalidException;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }


    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }
    public void delete(Long id){
        if(!this.resumeRepository.existsById(id)){
            throw new IdInvalidException("Resume with ID:" + id + " not found");
        }
        this.resumeRepository.deleteById(id);
    }
    public ResCreateResumeDTO create(Resume resume) throws IdInvalidException {
        boolean isIdExist = this.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User id/Job id không tồn tại");
        }

        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedBy(resume.getCreatedBy());
        res.setCreatedAt(resume.getCreatedAt());

        return res;
    }
    public ResUpdateResumeDTO update(Long id, Resume resume) {
        Resume existingResume = this.fetchById(id);
        existingResume.setStatus(resume.getStatus());
        Resume updatedResume = this.resumeRepository.save(existingResume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(updatedResume.getUpdatedAt());
        res.setUpdatedBy(updatedResume.getUpdatedBy());
        return res;
    }

    public Resume fetchById(long id) {
        return this.resumeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Could not find resume"));
    }
    public ResFetchResumeDTO getResume(Long id) {
        Resume resume = fetchById(id);
        return mapToResFetchResumeDTO(resume);
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageUser = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = pageUser.getContent()
                .stream().map(item -> this.getResume(item.getId()))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }


    private ResFetchResumeDTO mapToResFetchResumeDTO(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        Optional.ofNullable(resume.getJob())
                .ifPresent(job -> {
                    res.setCompanyName(job.getCompany().getName());
                    res.setJob(new ResFetchResumeDTO.JobResume(job.getId(), job.getName()));
                });
        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));

        return res;
    }
    public ResultPaginationDTO getResumesByUser(Pageable pageable){
        String email= SecurityUtil.getCurrentUserLogin().isPresent()
                ?SecurityUtil.getCurrentUserLogin().get()
                :"";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.getResume(item.getId()))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }
}
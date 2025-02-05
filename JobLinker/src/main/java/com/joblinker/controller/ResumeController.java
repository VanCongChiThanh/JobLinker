package com.joblinker.controller;

import com.joblinker.domain.Company;
import com.joblinker.domain.Job;
import com.joblinker.domain.Resume;
import com.joblinker.domain.User;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.domain.response.Resume.ResCreateResumeDTO;
import com.joblinker.domain.response.Resume.ResFetchResumeDTO;
import com.joblinker.domain.response.Resume.ResUpdateResumeDTO;
import com.joblinker.service.ResumeService;
import com.joblinker.service.UserService;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }
    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume){
        // create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }
    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) {

        return ResponseEntity.ok().body(this.resumeService.getResume(id));
    }
    @PutMapping("/resumes/{id}")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(
            @PathVariable Long id,
            @RequestBody Resume resume) {
        return ResponseEntity.ok().body(this.resumeService.update(id, resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

        List<Long> arrJobIds;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userService.getUserbyEmail(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs.stream().map(Job::getId).collect(Collectors.toList());
                } else {
                    arrJobIds = null;
                }
            } else {
                arrJobIds = null;
            }
        } else {
            arrJobIds = null;
        }
        Specification<Resume> jobInSpec = (root, query, criteriaBuilder) -> {
            if (arrJobIds != null && !arrJobIds.isEmpty()) {
                return root.get("job").get("id").in(arrJobIds);
            }
            return criteriaBuilder.conjunction();
        };
        Specification<Resume> finalSpec = spec != null ? spec.and(jobInSpec) : jobInSpec;
        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/resumes/by-user")
    @ApiMessage("Get all resumes by user with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.getResumesByUser(pageable));
    }
}
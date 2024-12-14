package com.joblinker.controller;

import com.joblinker.domain.Job;
import com.joblinker.domain.response.Job.ResCreateJobDTO;
import com.joblinker.domain.response.Job.ResUpdateJobDTO;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.service.JobService;
import com.joblinker.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    @PostMapping("/jobs")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job){
        return ResponseEntity.ok().body(jobService.createJob(job));
    }
    @PutMapping("/jobs/{id}")
    public ResponseEntity<ResUpdateJobDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(id, job));
    }
    @GetMapping("/jobs/{jobId}")
    @ApiMessage("Get job by id")
    public ResponseEntity<Job> getJobById(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }
    @GetMapping("/jobs")
    @ApiMessage("Get job with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.jobService.getAllJobs(spec, pageable));
    }
    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/jobs/top-jobs")
    @ApiMessage("Get top hot jobs")
    public ResponseEntity<List<Job>> getTopJobs(){
        return ResponseEntity.ok(jobService.getTopJobsWithMostResumes(9));
    }
}
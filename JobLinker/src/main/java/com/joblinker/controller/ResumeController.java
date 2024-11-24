package com.joblinker.controller;

import com.joblinker.domain.Resume;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.domain.response.Resume.ResCreateResumeDTO;
import com.joblinker.domain.response.Resume.ResFetchResumeDTO;
import com.joblinker.domain.response.Resume.ResUpdateResumeDTO;
import com.joblinker.service.ResumeService;
import com.joblinker.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
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
    @ApiMessage("Get all resumes with pagination")
    public ResponseEntity<ResultPaginationDTO> getAll(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.getResumes(null, pageable));
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
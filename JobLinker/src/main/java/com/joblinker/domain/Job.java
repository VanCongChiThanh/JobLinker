package com.joblinker.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.constant.LevelEnum;
import com.joblinker.util.constant.LocationEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ManyToMany(fetch=FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs","createdAt","updatedAt","updatedBy","createdBy"})
    @JoinTable(name="job_skill",
            joinColumns =@JoinColumn(name="job_id"),
            inverseJoinColumns = @JoinColumn(name="skill_id"))
    List<Skill> skills;
    @ManyToOne
    @JsonIgnoreProperties(value = {"description","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "company_id")
    private Company company;
    private double salary;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private LevelEnum level;
    @Lob
    private String description;
    private Instant startTime;
    private Instant endTime;
    private boolean active;
    @Enumerated(EnumType.STRING)
    private LocationEnum location;

    @OneToMany(mappedBy ="job",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get():"";
    }
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get():"";
    }

}
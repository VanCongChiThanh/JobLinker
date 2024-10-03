package com.joblinker.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joblinker.util.SecurityUtil;
import com.joblinker.util.constant.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @NotBlank(message = "email is not empty")
    private String email;
    @NotBlank(message = "password is not empty")
    private String password;
    private int age;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String address;
    private String refreshToken;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss a",timezone="GMT+7")
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()==true ? SecurityUtil.getCurrentUserLogin().get():"";
    }
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() : "";
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    private String updatedBy;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
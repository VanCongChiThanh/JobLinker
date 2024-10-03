package com.joblinker.domain.dto;

import com.joblinker.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class ResCreateUserDTO {
    private Long id;

    public ResCreateUserDTO() {
    }

    public ResCreateUserDTO(Long id, String name, String email, int age, String address, GenderEnum gender, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.createdAt = createdAt;
    }

    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

package com.joblinker.domain.dto;

import com.joblinker.util.constant.GenderEnum;

import java.time.Instant;

public class ResUserDTO extends ResCreateUserDTO{
    private Instant updatedAt;

    public ResUserDTO(Long id, String name, String email, int age, String address, GenderEnum gender, Instant createdAt, Instant updatedAt) {
        super(id, name, email, age, address, gender, createdAt);
        this.updatedAt = updatedAt;
    }

    public ResUserDTO() {
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

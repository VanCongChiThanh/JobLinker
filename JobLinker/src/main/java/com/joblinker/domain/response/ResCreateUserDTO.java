package com.joblinker.domain.response;

import com.joblinker.util.constant.GenderEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}

package com.joblinker.domain.response.Job;

import com.joblinker.util.constant.LevelEnum;
import com.joblinker.util.constant.LocationEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;

    private LocationEnum location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    private Instant startDate;
    private Instant endDate;
    private boolean isActive;

    private List<String> skills;
    private Instant createdAt;
    private String createdBy;
}


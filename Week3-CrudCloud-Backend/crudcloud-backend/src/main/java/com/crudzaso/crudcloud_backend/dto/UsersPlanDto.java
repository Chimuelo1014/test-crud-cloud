package com.crudzaso.crudcloud_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersPlanDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private Long planId;
    private String planName;
    private int planMaxInstances;
    private String status;
    private Date startDate;
    private Date endDate;
}
package com.crudzaso.crudcloud_backend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CreateUsersPlanRequest {
    private Long userId;
    private Long planId;
    private String status; // "ACTIVE" o "INACTIVE"
    private Date startDate;
    private Date endDate;
}
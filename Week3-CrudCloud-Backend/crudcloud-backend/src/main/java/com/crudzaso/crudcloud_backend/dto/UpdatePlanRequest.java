package com.crudzaso.crudcloud_backend.dto;

import lombok.Data;

@Data
public class UpdatePlanRequest {
    private String name;
    private Integer maxInstances;
    private Integer priceIdMercadoPago;
    private String description;
    private String state;
}
package com.crudzaso.crudcloud_backend.dto;

import lombok.Data;

@Data
public class CreatePlanRequest {
    private String name;
    private int maxInstances;
    private int priceIdMercadoPago;
    private String description;
    private String state; // "ACTIVE" o "INACTIVE"
}
package com.crudzaso.crudcloud_backend.dto;

public class PlanDto {
    private Long id;
    private String name;
    private int maxInstances;
    private String description;
    private String state;

    public PlanDto(Long id, String name, int maxInstances, String description, String state) {
        this.id = id;
        this.name = name;
        this.maxInstances = maxInstances;
        this.description = description;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxInstances() {
        return maxInstances;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }
    
    
    
}

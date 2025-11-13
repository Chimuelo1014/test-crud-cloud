package com.crudzaso.crudcloud_backend.service;

import com.crudzaso.crudcloud_backend.dto.CreatePlanRequest;
import com.crudzaso.crudcloud_backend.dto.PlanDto;
import com.crudzaso.crudcloud_backend.dto.UpdatePlanRequest;
import com.crudzaso.crudcloud_backend.model.Plan;
import com.crudzaso.crudcloud_backend.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    // CREATE
    public PlanDto createPlan(CreatePlanRequest request) {
        Plan plan = Plan.builder()
                .name(request.getName())
                .maxInstances(request.getMaxInstances())
                .priceIdMercadoPago(request.getPriceIdMercadoPago())
                .description(request.getDescription())
                .state(request.getState() != null ? request.getState() : "ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Plan saved = planRepository.save(plan);
        return convertToDTO(saved);
    }

    // READ - Todos los planes activos
    public List<PlanDto> getAllPlans() {
        return planRepository.findByState("ACTIVE")
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // READ - Plan por ID
    public PlanDto getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
        return convertToDTO(plan);
    }

    // UPDATE
    public PlanDto updatePlan(Long id, UpdatePlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));

        if (request.getName() != null) {
            plan.setName(request.getName());
        }
        if (request.getMaxInstances() != null) {
            plan.setMaxInstances(request.getMaxInstances());
        }
        if (request.getPriceIdMercadoPago() != null) {
            plan.setPriceIdMercadoPago(request.getPriceIdMercadoPago());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getState() != null) {
            plan.setState(request.getState());
        }

        plan.setUpdatedAt(LocalDateTime.now());
        Plan updated = planRepository.save(plan);
        return convertToDTO(updated);
    }

    // DELETE
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new RuntimeException("Plan not found with id: " + id);
        }
        planRepository.deleteById(id);
    }

    // Convertir entidad a DTO
    private PlanDto convertToDTO(Plan plan) {
        return new PlanDto(
                plan.getId(),
                plan.getName(),
                plan.getMaxInstances(),
                plan.getDescription(),
                plan.getState()
        );
    }
}
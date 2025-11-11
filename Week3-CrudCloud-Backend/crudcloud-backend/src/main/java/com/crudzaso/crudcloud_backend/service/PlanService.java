package com.crudzaso.crudcloud_backend.service;
import com.crudzaso.crudcloud_backend.dto.PlanDto;
import com.crudzaso.crudcloud_backend.model.Plan;
import com.crudzaso.crudcloud_backend.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    // Obtener todos los planes activos
    public List<PlanDto> getAllPlans() {
        return planRepository.findByState("ACTIVE")
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Buscar plan por ID
    public PlanDto getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));

        return convertToDTO(plan);
    }

    // Método para convertir entidad -> DTO
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

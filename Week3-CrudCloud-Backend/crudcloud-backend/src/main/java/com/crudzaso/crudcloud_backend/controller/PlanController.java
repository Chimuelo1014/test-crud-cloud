package com.crudzaso.crudcloud_backend.controller;

import com.crudzaso.crudcloud_backend.dto.CreatePlanRequest;
import com.crudzaso.crudcloud_backend.dto.PlanDto;
import com.crudzaso.crudcloud_backend.dto.UpdatePlanRequest;
import com.crudzaso.crudcloud_backend.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // CREATE - Crear nuevo plan (Solo Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDto> createPlan(@RequestBody CreatePlanRequest request) {
        PlanDto created = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ - Obtener todos los planes activos
    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    // READ - Obtener plan por ID
    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    // UPDATE - Actualizar plan (Solo Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDto> updatePlan(
            @PathVariable Long id,
            @RequestBody UpdatePlanRequest request) {
        PlanDto updated = planService.updatePlan(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Eliminar plan (Solo Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
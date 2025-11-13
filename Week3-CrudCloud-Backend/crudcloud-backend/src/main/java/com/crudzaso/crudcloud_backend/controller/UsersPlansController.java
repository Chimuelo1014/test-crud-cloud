package com.crudzaso.crudcloud_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.crudzaso.crudcloud_backend.dto.CreateUsersPlanRequest;
import com.crudzaso.crudcloud_backend.dto.UsersPlanDto;
import com.crudzaso.crudcloud_backend.model.UsersPlans;
import com.crudzaso.crudcloud_backend.service.UsersPlansService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users Plans", description = "Manage user subscriptions to plans")
@RestController
@RequestMapping("/api/users-plans")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsersPlansController {
    
    private final UsersPlansService usersPlansService;

    // ======================== CREAR =========================
    @Operation(summary = "Create a new user plan subscription")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UsersPlanDto> createUserPlan(@RequestBody CreateUsersPlanRequest request) {
        UsersPlanDto created = usersPlansService.createUserPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ======================== OBTENER TODOS (Admin) =========================
    @Operation(summary = "Get all user plans (Admin only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsersPlanDto>> getAllUsersPlans() {
        return ResponseEntity.ok(usersPlansService.getAllUsersPlans());
    }

    // ======================== FILTROS =========================
    @Operation(summary = "Get plans by user ID")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UsersPlanDto>> getPlansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(usersPlansService.getPlansByUserId(userId));
    }

    @Operation(summary = "Get all active plans")
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsersPlanDto>> getActivePlans() {
        return ResponseEntity.ok(usersPlansService.getActivePlans());
    }

    @Operation(summary = "Get all inactive plans")
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsersPlanDto>> getInactivePlans() {
        return ResponseEntity.ok(usersPlansService.getInactivePlans());
    }

    @Operation(summary = "Get active plans for a specific user")
    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UsersPlanDto>> getActivePlansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(usersPlansService.getActivePlansByUser(userId));
    }

    // ======================== ACTUALIZAR =========================
    @Operation(summary = "Update a user plan")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsersPlanDto> updateUsersPlan(
            @PathVariable Long id,
            @RequestBody UsersPlans updatedPlan
    ) {
        return usersPlansService.updateUsersPlan(id, updatedPlan)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ======================== ELIMINAR =========================
    @Operation(summary = "Delete a user plan")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserPlan(@PathVariable Long id) {
        boolean deleted = usersPlansService.deleteUserPlan(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
package com.crudzaso.crudcloud_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crudzaso.crudcloud_backend.model.UsersPlans;
import com.crudzaso.crudcloud_backend.service.UsersPlansService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class UsersPlansController {
    private UsersPlansService usersPlansService;

    public UsersPlansController(UsersPlansService usersPlansService){
        this.usersPlansService = usersPlansService;
    }   
    
    @GetMapping
    public List<UsersPlans> getAll() {
        return usersPlansService.getAllUsersPlans(); // Only for Admin
    }

    // Planes de un usuario (todos)
    @GetMapping("/user/{userId}")
    public List<UsersPlans> getPlansByUser(@PathVariable Long userId) {
        return usersPlansService.getPlansByUserId(userId);
    }

    // Planes activos
    @GetMapping("/active")
    public List<UsersPlans> getActivePlans() {
        return usersPlansService.getActivePlans();
    }

    // Planes desactivados
    @GetMapping("/inactive")
    public List<UsersPlans> getInactivePlans() {
        return usersPlansService.getInactivePlans();
    }

    // Planes activos de un usuario específico
    @GetMapping("/user/{userId}/active")
    public List<UsersPlans> getActivePlansByUser(@PathVariable Long userId) {
        return usersPlansService.getActivePlansByUser(userId);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsersPlans> updateUsersPlan(
            @PathVariable Long id,
            @RequestBody UsersPlans updatedPlan
    ) {
        return usersPlansService.updateUsersPlan(id, updatedPlan)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

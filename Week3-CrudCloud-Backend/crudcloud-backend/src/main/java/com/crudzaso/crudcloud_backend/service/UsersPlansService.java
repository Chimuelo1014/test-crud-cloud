package com.crudzaso.crudcloud_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crudzaso.crudcloud_backend.dto.CreateUsersPlanRequest;
import com.crudzaso.crudcloud_backend.dto.UsersPlanDto;
import com.crudzaso.crudcloud_backend.model.Plan;
import com.crudzaso.crudcloud_backend.model.User;
import com.crudzaso.crudcloud_backend.model.UsersPlans;
import com.crudzaso.crudcloud_backend.repository.PlanRepository;
import com.crudzaso.crudcloud_backend.repository.UserRepository;
import com.crudzaso.crudcloud_backend.repository.UsersPlansRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersPlansService {
    
    private final UsersPlansRepository usersPlansRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    // Crear un nuevo plan para un usuario
    @Transactional
    public UsersPlanDto createUserPlan(CreateUsersPlanRequest request) {
        // Validar que el usuario existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Validar que el plan existe
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + request.getPlanId()));

        // Crear la relación
        UsersPlans usersPlan = UsersPlans.builder()
                .user(user)
                .plan(plan)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        UsersPlans saved = usersPlansRepository.save(usersPlan);
        return convertToDTO(saved);
    }

    // Obtener todos los planes (solo para Admin)
    public List<UsersPlanDto> getAllUsersPlans() {
        return usersPlansRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener planes de un usuario específico
    public List<UsersPlanDto> getPlansByUserId(Long userId) {
        return usersPlansRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener todos los planes activos
    public List<UsersPlanDto> getActivePlans() {
        return usersPlansRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener todos los planes inactivos
    public List<UsersPlanDto> getInactivePlans() {
        return usersPlansRepository.findByStatusNot("ACTIVE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener planes activos de un usuario específico
    public List<UsersPlanDto> getActivePlansByUser(Long userId) {
        return usersPlansRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Actualizar un plan de usuario
    @Transactional
    public Optional<UsersPlanDto> updateUsersPlan(Long id, UsersPlans updatedPlan) {
        return usersPlansRepository.findById(id).map(existingPlan -> {
            if (updatedPlan.getStatus() != null) {
                existingPlan.setStatus(updatedPlan.getStatus());
            }
            if (updatedPlan.getStartDate() != null) {
                existingPlan.setStartDate(updatedPlan.getStartDate());
            }
            if (updatedPlan.getEndDate() != null) {
                existingPlan.setEndDate(updatedPlan.getEndDate());
            }
            UsersPlans saved = usersPlansRepository.save(existingPlan);
            return convertToDTO(saved);
        });
    }

    // Eliminar un plan de usuario
    @Transactional
    public boolean deleteUserPlan(Long id) {
        if (usersPlansRepository.existsById(id)) {
            usersPlansRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Convertir entidad a DTO
    private UsersPlanDto convertToDTO(UsersPlans usersPlans) {
        return UsersPlanDto.builder()
                .id(usersPlans.getId())
                .userId(usersPlans.getUser().getId())
                .userEmail(usersPlans.getUser().getEmail())
                .userFullName(usersPlans.getUser().getFullName())
                .planId(usersPlans.getPlan().getId())
                .planName(usersPlans.getPlan().getName())
                .planMaxInstances(usersPlans.getPlan().getMaxInstances())
                .status(usersPlans.getStatus())
                .startDate(usersPlans.getStartDate())
                .endDate(usersPlans.getEndDate())
                .build();
    }
}
package com.crudzaso.crudcloud_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.crudzaso.crudcloud_backend.model.UsersPlans;
import com.crudzaso.crudcloud_backend.repository.UsersPlansRepository;
@Service
public class UsersPlansService {
    private UsersPlansRepository usersPlansRepository;

    public UsersPlansService(UsersPlansRepository usersPlansRepository){
        this.usersPlansRepository = usersPlansRepository;
    }
    
    public List<UsersPlans> getAllUsersPlans() {
        return usersPlansRepository.findAll();
    }

    public List<UsersPlans> getPlansByUserId(Long userId) {
        return usersPlansRepository.findByUserId(userId);
    }

    public List<UsersPlans> getActivePlans() {
        return usersPlansRepository.findByStatus("ACTIVE");
    }

    public List<UsersPlans> getInactivePlans() {
        return usersPlansRepository.findByStatusNot("ACTIVE");
    }

    public List<UsersPlans> getActivePlansByUser(Long userId) {
        return usersPlansRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }

    public Optional<UsersPlans> updateUsersPlan(Long id, UsersPlans updatedPlan) { //editar plan del usuario
        return usersPlansRepository.findById(id).map(existingPlan -> {
            // Solo actualizamos los campos que queramos permitir
            if (updatedPlan.getStatus() != null) {
                existingPlan.setStatus(updatedPlan.getStatus());
            }
            if (updatedPlan.getStartDate() != null) {
                existingPlan.setStartDate(updatedPlan.getStartDate());
            }
            if (updatedPlan.getEndDate() != null) {
                existingPlan.setEndDate(updatedPlan.getEndDate());
            }
            return usersPlansRepository.save(existingPlan);
        });
    }
}

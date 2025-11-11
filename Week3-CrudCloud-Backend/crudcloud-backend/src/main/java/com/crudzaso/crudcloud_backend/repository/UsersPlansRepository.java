package com.crudzaso.crudcloud_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crudzaso.crudcloud_backend.model.UsersPlans;

public interface UsersPlansRepository extends JpaRepository<UsersPlans, Long>{

    // Obtener todos los planes de un usuario (independiente del estado)
    List<UsersPlans> findByUserId(Long userId);

    // Obtener planes activos de un usuario
    List<UsersPlans> findByUserIdAndStatus(Long userId, String status);

    // Obtener todos los planes activos
    List<UsersPlans> findByStatus(String status);

    // Obtener todos los planes desactivados
    List<UsersPlans> findByStatusNot(String status); // o "INACTIVE" si lo defines así

}

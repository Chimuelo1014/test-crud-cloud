package com.crudzaso.crudcloud_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crudzaso.crudcloud_backend.model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>{
    List<Plan> findByState(String state);//for Admin
    Plan findByName(String name); 
}

package com.crudzaso.crudcloud_backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "max_instances", nullable = false)
    private int maxInstances;

    @Column(name = "price_id_mercadopago", nullable = false)
    private int priceIdMercadoPago;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "state", nullable = false)
    private String state; // "ACTIVE" o "INACTIVE"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Evitar recursión infinita en JSON
    private Set<UsersPlans> usersPlans = new HashSet<>();

    // Método auxiliar para establecer timestamps automáticamente
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (state == null) {
            state = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
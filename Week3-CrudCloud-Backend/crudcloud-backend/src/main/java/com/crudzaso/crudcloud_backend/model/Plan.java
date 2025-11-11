package com.crudzaso.crudcloud_backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "name")
    private String name;

    @Column(name = "max_instances")
    private int maxInstances;

    @Column(name = "price_id_mercadopago")
    private int priceIdMercadoPago;
    
    private String description;

    @Column(name = "state")
    private String state;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "plans", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersPlans> usersPlans = new HashSet<>();
}

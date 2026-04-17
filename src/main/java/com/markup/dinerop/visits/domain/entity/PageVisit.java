package com.markup.dinerop.visits.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "page_visits")
@Data
public class PageVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fila única que acumula el total global. Siempre habrá exactamente 1 registro.
    @Column(name = "total_visits", nullable = false)
    private Long totalVisits = 2000L;
}

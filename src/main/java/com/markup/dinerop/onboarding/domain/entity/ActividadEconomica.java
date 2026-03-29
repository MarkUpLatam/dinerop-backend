package com.markup.dinerop.onboarding.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "actividad_economica")
public class ActividadEconomica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "persona_id")
    private PersonaOnboarding persona;

    private String nombreNegocio;
    private String direccionNegocio;
    private String tiempoActividad;
    private String telefonoNegocio;
}

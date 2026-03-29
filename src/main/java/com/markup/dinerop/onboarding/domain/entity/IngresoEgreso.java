package com.markup.dinerop.onboarding.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ingreso_egreso")
public class IngresoEgreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "persona_id")
    private PersonaOnboarding persona;

    private Double ingresoMensual;
    private Double egresoMensual;
}

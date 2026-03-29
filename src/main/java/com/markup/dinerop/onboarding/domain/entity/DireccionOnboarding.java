package com.markup.dinerop.onboarding.domain.entity;

import com.markup.dinerop.onboarding.domain.enums.TipoVivienda;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "direccion_onboarding")
public class DireccionOnboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "persona_id")
    private PersonaOnboarding persona;

    private String provincia;
    private String canton;
    private String barrio;
    private String callePrincipal;
    private String numero;
    private String referenciaUbicacion;

    @Enumerated(EnumType.STRING)
    private TipoVivienda tipoVivienda;
}

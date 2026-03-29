package com.markup.dinerop.onboarding.domain.entity;

import com.markup.dinerop.onboarding.domain.enums.TipoReferencia;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "referencia_personal")
public class ReferenciaPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "persona_id")
    private PersonaOnboarding persona;

    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    private TipoReferencia tipo;

    private String parentesco;
    private String telefono;
}

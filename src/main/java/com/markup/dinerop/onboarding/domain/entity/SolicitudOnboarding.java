package com.markup.dinerop.onboarding.domain.entity;

import com.markup.dinerop.onboarding.domain.enums.EstadoOnboarding;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "solicitud_onboarding")
public class SolicitudOnboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private String destinoCredito;

    @Enumerated(EnumType.STRING)
    private EstadoOnboarding estado;

    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PersonaOnboarding> personas = new ArrayList<>();

    @PrePersist
    void prePersist() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = EstadoOnboarding.EN_PROCESO;
    }
}

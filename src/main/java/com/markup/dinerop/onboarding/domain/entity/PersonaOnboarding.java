package com.markup.dinerop.onboarding.domain.entity;

import com.markup.dinerop.onboarding.domain.enums.EstadoFormulario;
import com.markup.dinerop.onboarding.domain.enums.RolPersona;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "persona_onboarding")
public class PersonaOnboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private SolicitudOnboarding solicitud;

    @Enumerated(EnumType.STRING)
    private RolPersona rol;

    @Enumerated(EnumType.STRING)
    private EstadoFormulario estadoFormulario;

    private String nombres;
    private String apellidos;
    private String cedula;
    private LocalDate fechaNacimiento;
    private String estadoCivil;
    private String ocupacion;
    private String empresaTrabajo;
    private String telefono;
    private LocalDateTime fechaRegistro;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private DireccionOnboarding direccion;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private ActividadEconomica actividadEconomica;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private IngresoEgreso ingresoEgreso;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReferenciaPersonal> referencias;

    @PrePersist
    void prePersist() {
        fechaRegistro = LocalDateTime.now();
        if (estadoFormulario == null) estadoFormulario = EstadoFormulario.PENDIENTE;
    }
}

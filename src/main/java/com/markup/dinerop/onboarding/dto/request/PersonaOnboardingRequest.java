package com.markup.dinerop.onboarding.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaOnboardingRequest {

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @NotBlank
    private String cedula;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotBlank
    private String estadoCivil;

    private String ocupacion;
    private String empresaTrabajo;
    private String telefono;

    @NotNull
    private Boolean tieneConyuge;

    @Valid
    @NotNull
    private DireccionRequest direccion;

    @Valid
    private ActividadEconomicaRequest actividadEconomica;

    @Valid
    @NotNull
    private IngresoEgresoRequest ingresoEgreso;

    @Valid
    private List<ReferenciaRequest> referencias;
}

package com.markup.dinerop.onboarding.infrastructure.mapper;

import com.markup.dinerop.onboarding.domain.entity.*;
import com.markup.dinerop.onboarding.dto.request.*;
import com.markup.dinerop.onboarding.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OnboardingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solicitud", ignore = true)
    @Mapping(target = "estadoFormulario", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    PersonaOnboarding toPersona(PersonaOnboardingRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persona", ignore = true)
    DireccionOnboarding toDireccion(DireccionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persona", ignore = true)
    ActividadEconomica toActividadEconomica(ActividadEconomicaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persona", ignore = true)
    IngresoEgreso toIngresoEgreso(IngresoEgresoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persona", ignore = true)
    ReferenciaPersonal toReferencia(ReferenciaRequest request);

    List<ReferenciaPersonal> toReferencias(List<ReferenciaRequest> requests);

    SolicitudOnboardingResponse toSolicitudResponse(SolicitudOnboarding entity);

    PersonaOnboardingResponse toPersonaResponse(PersonaOnboarding entity);

    List<PersonaOnboardingResponse> toPersonasResponse(List<PersonaOnboarding> entities);

    @Mapping(source = "id", target = "solicitudId")
    @Mapping(source = "estado", target = "estado")
    @Mapping(source = "destinoCredito", target = "destinoCredito")
    @Mapping(source = "personas", target = "personas")
    OnboardingUnificadoResponse toOnboardingUnificadoResponse(SolicitudOnboarding entity);
}

package com.markup.dinerop.cooperative.domain.service;

import com.markup.dinerop.cooperative.domain.entity.Cooperative;
import com.markup.dinerop.cooperative.domain.entity.CooperativeRate;
import com.markup.dinerop.cooperative.domain.entity.enums.CreditType;
import com.markup.dinerop.cooperative.domain.repository.CooperativeRateRepository;
import com.markup.dinerop.cooperative.domain.repository.CooperativeRepository;
import com.markup.dinerop.cooperative.dto.CooperativeDto;
import com.markup.dinerop.cooperative.dto.CooperativeRateDto;
import com.markup.dinerop.cooperative.dto.CreateCooperativeDto;
import com.markup.dinerop.cooperative.dto.InternalCooperativeDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CooperativeService {

    private final CooperativeRepository cooperativeRepository;
    private final CooperativeRateRepository cooperativeRateRepository;

    public List<CooperativeDto> getAll() {
        return cooperativeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<CooperativeDto> findByCity(String city) {
        return cooperativeRepository.findByCiudadIgnoreCase(city)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<CooperativeDto> findByProvince(String province) {
        return cooperativeRepository.findByProvinciaIgnoreCase(province)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Cooperative getById(Long id) {
        return cooperativeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cooperativa no encontrada con ID: " + id));
    }

    public CooperativeDto getCooperativeDtoById(Long id) {
        return toDto(getById(id));
    }

    public CooperativeDto create(CreateCooperativeDto dto) {
        Cooperative cooperative = new Cooperative();
        cooperative.setNombre(dto.getNombre());
        cooperative.setCiudad(dto.getCiudad());
        cooperative.setProvincia(dto.getProvincia());
        cooperative.setDireccion(dto.getDireccion());
        cooperative.setTelefono(dto.getTelefono());
        cooperative.setPaginaWeb(dto.getPaginaWeb());
        cooperative.setLogoUrl(dto.getLogoUrl());
        cooperative.setCalificacion(dto.getCalificacion());
        cooperative.setMontoMaximoCredito(
                dto.getMontoMaximoCredito() != null ? dto.getMontoMaximoCredito() : BigDecimal.ZERO
        );

        return toDto(cooperativeRepository.save(cooperative));
    }

    public List<InternalCooperativeDto> findInternalByCity(String city) {
        return cooperativeRepository.findByCiudadIgnoreCase(city)
                .stream()
                .map(this::toInternalDto)
                .toList();
    }

    public InternalCooperativeDto getInternalById(Long id) {
        return toInternalDto(getById(id));
    }

    public List<InternalCooperativeDto> findInternalEligible(String city, BigDecimal minAmount) {
        String safeCity = city == null ? "" : city.trim();
        BigDecimal safeAmount = minAmount == null ? BigDecimal.ZERO : minAmount;

        return cooperativeRepository
                .findByCiudadIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(safeCity, safeAmount)
                .stream()
                .map(this::toInternalDto)
                .toList();
    }

    public List<Cooperative> getEligibleCooperatives(String ciudad, String provincia, Double monto) {
        BigDecimal safeAmount = monto == null ? BigDecimal.ZERO : BigDecimal.valueOf(monto);
        String safeCity = ciudad == null ? "" : ciudad.trim();
        String safeProvince = provincia == null ? "" : provincia.trim();

        if (!safeCity.isBlank() && !safeProvince.isBlank()) {
            return cooperativeRepository
                    .findByCiudadIgnoreCaseAndProvinciaIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(
                            safeCity,
                            safeProvince,
                            safeAmount
                    );
        }

        if (!safeCity.isBlank()) {
            return cooperativeRepository
                    .findByCiudadIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(safeCity, safeAmount);
        }

        if (!safeProvince.isBlank()) {
            return cooperativeRepository
                    .findByProvinciaIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(safeProvince, safeAmount);
        }

        return cooperativeRepository.findAll()
                .stream()
                .filter(cooperative -> cooperative.getMontoMaximoCredito() != null)
                .filter(cooperative -> cooperative.getMontoMaximoCredito().compareTo(safeAmount) >= 0)
                .toList();
    }

    public Double getActiveRate(Long cooperativeId, CreditType creditType) {
        return getActiveRateEntity(cooperativeId, creditType).getTasaAnual().doubleValue();
    }

    public Double getActiveRate(
            Long cooperativeId,
            com.markup.dinerop.credit.domain.model.enums.CreditType creditType
    ) {
        return getActiveRate(cooperativeId, CreditType.valueOf(creditType.name()));
    }

    public CooperativeRateDto getActiveRateDto(Long cooperativeId, CreditType creditType) {
        CooperativeRate rate = getActiveRateEntity(cooperativeId, creditType);
        return new CooperativeRateDto(cooperativeId, creditType, rate.getTasaAnual());
    }

    private CooperativeRate getActiveRateEntity(Long cooperativeId, CreditType creditType) {
        return cooperativeRateRepository
                .findByCooperativaIdAndTipoCreditoAndActivaTrue(cooperativeId, creditType)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe tasa activa para cooperativaId=" + cooperativeId +
                                " y tipoCredito=" + creditType
                ));
    }

    private CooperativeDto toDto(Cooperative cooperative) {
        CooperativeDto dto = new CooperativeDto();
        dto.setId(cooperative.getId());
        dto.setNombre(cooperative.getNombre());
        dto.setCiudad(cooperative.getCiudad());
        dto.setProvincia(cooperative.getProvincia());
        dto.setDireccion(cooperative.getDireccion());
        dto.setTelefono(cooperative.getTelefono());
        dto.setPaginaWeb(cooperative.getPaginaWeb());
        dto.setLogoUrl(cooperative.getLogoUrl());
        dto.setCalificacion(cooperative.getCalificacion());
        dto.setMontoMaximoCredito(cooperative.getMontoMaximoCredito());
        return dto;
    }

    private InternalCooperativeDto toInternalDto(Cooperative cooperative) {
        InternalCooperativeDto dto = new InternalCooperativeDto();
        dto.setId(cooperative.getId());
        dto.setNombre(cooperative.getNombre());
        dto.setCiudad(cooperative.getCiudad());
        dto.setMontoMaximoCredito(cooperative.getMontoMaximoCredito());
        return dto;
    }
}

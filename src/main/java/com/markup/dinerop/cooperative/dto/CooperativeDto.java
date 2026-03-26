package com.markup.dinerop.cooperative.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CooperativeDto {
    private Long id;
    private String nombre;
    private String ciudad;
    private String provincia;
    private String direccion;
    private String telefono;
    private String paginaWeb;
    private String logoUrl;
    private Double calificacion;
    private BigDecimal montoMaximoCredito;

}

package com.markup.dinerop.cooperative.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InternalCooperativeDto {

    private Long id;
    private String nombre;
    private String ciudad;
    private BigDecimal montoMaximoCredito;


}

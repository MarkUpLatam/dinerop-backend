package com.markup.dinerop.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CooperativeDto {
    private Long id;
    private String nombre;
    private String ciudad;
    private String provincia;
    private String email;
    private String telefono;

    public CooperativeDto(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}



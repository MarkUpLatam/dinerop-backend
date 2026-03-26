package com.markup.dinerop.cooperative.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "cooperativa")
@Data
public class Cooperative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cooperativa")
    private Long id;

    @Column(name = "id")
    private Long legacyId;

    private String nombre;
    private String ciudad;
    private String provincia;
    private String direccion;
    private String telefono;

    @Column(name = "pagina_web")
    private String paginaWeb;

    @Column(name = "logo_url")
    private String logoUrl;

    private Double calificacion;

    @Column(name = "monto_maximo_credito", nullable = false, precision = 38, scale = 2)
    private BigDecimal montoMaximoCredito = BigDecimal.ZERO;
}

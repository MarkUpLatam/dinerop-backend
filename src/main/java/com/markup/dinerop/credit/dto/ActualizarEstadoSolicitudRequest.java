package com.markup.dinerop.credit.dto;

import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import lombok.Data;

@Data
public class ActualizarEstadoSolicitudRequest {
    private Long cooperativaId;
    private SolicitudCooperativaStatus nuevoEstado;
}


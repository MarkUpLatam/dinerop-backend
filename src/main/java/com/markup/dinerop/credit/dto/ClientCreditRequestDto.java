package com.markup.dinerop.credit.dto;

import com.markup.dinerop.credit.domain.model.enums.CreditRequestType;
import com.markup.dinerop.credit.domain.model.enums.CreditType;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreditRequestDto {
    private BigDecimal monto;
    private CreditRequestType type;
    private CreditType creditType; // null si type == INVERSION
    private Integer plazoMeses;    // opcional
}

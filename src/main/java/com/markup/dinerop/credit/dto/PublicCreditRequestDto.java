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
public class PublicCreditRequestDto {
    //Identidad
    private String firstName;
    private String lastName;
    private String identification;

    //Contacto
    private String email;
    private String phone;

    //Ubicacion
    private String province;
    private String city;
    //Credito o inversion
    private BigDecimal amount;
    private Integer plazoMeses;
    private CreditRequestType type;
    private CreditType creditType;

}

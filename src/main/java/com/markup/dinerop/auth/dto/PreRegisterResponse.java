package com.markup.dinerop.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegisterResponse {
    private String activationToken; // puede ser null si ya estÃ¡ ACTIVE
}

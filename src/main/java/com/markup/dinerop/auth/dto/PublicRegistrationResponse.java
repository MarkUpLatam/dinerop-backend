package com.markup.dinerop.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicRegistrationResponse {

    private String email;
    private String message;
}

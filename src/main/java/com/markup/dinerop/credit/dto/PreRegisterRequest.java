package com.markup.dinerop.credit.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegisterRequest {
    private String email;
    private String role; // CLIENT
}

package com.markup.dinerop.credit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkClientRequestDto {

    @Email
    @NotNull
    private String email;

    @NotNull
    private Long clientId;
}



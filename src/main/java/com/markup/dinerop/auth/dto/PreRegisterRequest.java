package com.markup.dinerop.auth.dto;

import com.markup.dinerop.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Role role; // "CLIENT" | "COOPERATIVE"
}

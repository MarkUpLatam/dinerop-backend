package com.markup.dinerop.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkClientRequest {
    private String email;
    private Long clientId;
}

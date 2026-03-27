package com.markup.dinerop.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailDto {
    private String to;
    private String subject;
    private String htmlContent;
}

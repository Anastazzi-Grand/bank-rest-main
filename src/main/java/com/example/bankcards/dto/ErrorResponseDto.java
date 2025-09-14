package com.example.bankcards.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String code;
    private String message;
}

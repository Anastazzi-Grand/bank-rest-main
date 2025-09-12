package com.example.bankcards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardTransferRequestDto {

    @NotNull(message = "Source card ID is required")
    @Min(value = 1, message = "Source card ID must be positive")
    private Long fromCardId;

    @NotNull(message = "Target card ID is required")
    @Min(value = 1, message = "Target card ID must be positive")
    private Long toCardId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}

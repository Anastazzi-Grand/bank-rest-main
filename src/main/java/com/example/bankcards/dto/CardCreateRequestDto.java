package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCreateRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @Positive(message = "Balance must be positive")
    private BigDecimal balance = BigDecimal.ZERO;
}

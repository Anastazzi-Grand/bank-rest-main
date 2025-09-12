package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardStatusUpdateRequestDto {

    @NotNull(message = "Status is required")
    private Status status;
}
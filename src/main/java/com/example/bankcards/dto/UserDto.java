package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private Role role;
    private LocalDateTime createdAt;
}

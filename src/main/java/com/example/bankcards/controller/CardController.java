package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Validated
public class CardController {

    private final CardService cardService;

    /**
     * Создание карты (только ADMIN)
     */
    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardCreateRequestDto request) {
        // Позже: проверка роли через @PreAuthorize("hasRole('ADMIN')")
        CardDto cardDto = cardService.createCard(request);
        return ResponseEntity.status(201).body(cardDto);
    }

    /**
     * Получение всех карт пользователя (для USER и ADMIN)
     */
    @GetMapping("/my")
    public ResponseEntity<PageDto<CardDto>> getUserCards(
            @RequestParam Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        // Позже: проверим, что userId == currentUser.getId() для USER
        PageDto<CardDto> cards = cardService.getUserCards(userId, pageable);
        return ResponseEntity.ok(cards);
    }

    /**
     * Перевод между картами (для USER)
     */
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferMoney(
            @Valid @RequestBody CardTransferRequestDto request,
            User currentUser) { // Позже: будет подставляться автоматически
        cardService.transferMoney(request, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Блокировка карты (для USER и ADMIN)
     */
    @PatchMapping("/{id}/block")
    public ResponseEntity<CardDto> blockCard(@PathVariable Long id, User currentUser) {
        CardDto cardDto = cardService.blockCard(id, currentUser);
        return ResponseEntity.ok(cardDto);
    }

    /**
     * Активация карты (для ADMIN)
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CardDto> activateCard(@PathVariable Long id, User currentUser) {
        CardDto cardDto = cardService.activateCard(id, currentUser);
        return ResponseEntity.ok(cardDto);
    }

    /**
     * Удаление карты (для ADMIN)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение всех карт в системе (для ADMIN)
     */
    @GetMapping
    public ResponseEntity<PageDto<CardDto>> getAllCards(
            @PageableDefault(size = 10) Pageable pageable) {
        PageDto<CardDto> cards = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }
}

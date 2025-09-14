package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.*;
import com.example.bankcards.util.CardEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    private final CardEncryptionUtil encryptionUtil;

    /**
     * Создание новой карты (только админ)
     */
    @Transactional
    public CardDto createCard(CardCreateRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String encryptedNumber = encryptCardNumber(request.getCardNumber());
        String mask = maskCardNumber(request.getCardNumber());

        Card card = Card.builder()
                .user(user)
                .cardNumberEncrypted(encryptedNumber)
                .cardMask(mask)
                .expiryDate(request.getExpiryDate())
                .status(Status.ACTIVE)
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .build();

        Card savedCard = cardRepository.save(card);
        return toDto(savedCard);
    }

    /**
     * Получение всех карт пользователя с пагинацией
     */
    public PageDto<CardDto> getUserCards(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Page<Card> cardPage = cardRepository.findByUser(user, pageable);

        return PageDto.<CardDto>builder()
                .content(cardPage.getContent().stream().map(this::toDto).toList())
                .pageNumber(cardPage.getNumber())
                .pageSize(cardPage.getSize())
                .totalElements(cardPage.getTotalElements())
                .totalPages(cardPage.getTotalPages())
                .first(cardPage.isFirst())
                .last(cardPage.isLast())
                .build();
    }

    /**
     * Перевод между картами пользователя
     */
    @Transactional
    public void transferMoney(CardTransferRequestDto request, User currentUser) {
        // Проверяем, что карты не одинаковые
        if (request.getFromCardId().equals(request.getToCardId())) {
            throw new TransferBetweenSameCardException("Cannot transfer to the same card");
        }

        Card fromCard = getActiveCardForUser(request.getFromCardId(), currentUser);
        Card toCard = getActiveCardForUser(request.getToCardId(), currentUser);

        // Проверяем баланс
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds on card " + fromCard.getId());
        }

        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    /**
     * Блокировка карты (только админ)
     */
    @Transactional
    public CardDto blockCard(Long cardId, User currentUser) {
        Card card = cardRepository.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new CardNotFoundException("Card not found or access denied"));

        if (card.getStatus() == Status.BLOCKED) {
            throw new CardActionNotAllowedException("Card is already blocked");
        }

        card.setStatus(Status.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return toDto(savedCard);
    }

    /**
     * Активация карты (только админ)
     */
    @Transactional
    public CardDto activateCard(Long cardId, User currentUser) {
        Card card = cardRepository.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new CardNotFoundException("Card not found or access denied"));

        if (card.getStatus() == Status.EXPIRED) {
            throw new CardActionNotAllowedException("Cannot activate expired card");
        }

        if (card.getStatus() == Status.ACTIVE) {
            throw new CardActionNotAllowedException("Card is already active");
        }

        card.setStatus(Status.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return toDto(savedCard);
    }

    /**
     * Удаление карты (для админа и пользователя)
     */
    @Transactional
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new CardNotFoundException("Card not found");
        }
        cardRepository.deleteById(cardId);
    }

    /**
     * Получение всех карт в системе (для админа)
     */
    public PageDto<CardDto> getAllCards(Pageable pageable) {
        Page<Card> cardPage = cardRepository.findAll(pageable);
        return PageDto.<CardDto>builder()
                .content(cardPage.getContent().stream().map(this::toDto).toList())
                .pageNumber(cardPage.getNumber())
                .pageSize(cardPage.getSize())
                .totalElements(cardPage.getTotalElements())
                .totalPages(cardPage.getTotalPages())
                .first(cardPage.isFirst())
                .last(cardPage.isLast())
                .build();
    }

    /**
     * Вспомогательный метод: получить активную карту пользователя
     */
    private Card getActiveCardForUser(Long cardId, User user) {
        return cardRepository.findActiveCardByIdAndUser(cardId, user)
                .orElseThrow(() -> new CardNotFoundException("Card not found, not active, or not yours"));
    }

    /**
     * Шифрует номер карты с использованием AES-GCM
     */
    private String encryptCardNumber(String cardNumber) {
        return encryptionUtil.encrypt(cardNumber);
    }

    /**
     * Маскирует номер карты в формате: "**** **** **** 1234"
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }

        String cleanNumber = cardNumber.replaceAll("\\D", "");

        if (cleanNumber.length() < 4) {
            return "**** **** **** ****";
        }

        String last4 = cleanNumber.substring(cleanNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    /**
     * Маппинг Card → CardDto
     */
    private CardDto toDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .userId(card.getUser().getId())
                .cardMask(card.getCardMask())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

}

package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Card}.
 * Предоставляет методы для поиска карт по пользователю, статусу, с пагинацией и безопасным доступом.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Находит все карты указанного пользователя с поддержкой пагинации.
     *
     * @param user     владелец карт
     * @param pageable параметры пагинации (номер страницы, размер страницы)
     * @return страница с картами пользователя
     */
    Page<Card> findByUser(User user, Pageable pageable);

    /**
     * Находит все карты указанного пользователя (без пагинации).
     *
     * @param user владелец карт
     * @return список всех карт пользователя
     */
    List<Card> findByUser(User user);

    /**
     * Находит карту по её ID и владельцу.
     * Используется для обеспечения безопасности: пользователь может получить только свои карты.
     *
     * @param id   ID карты
     * @param user владелец карты
     * @return Optional с картой, если найдена и принадлежит пользователю; пустой Optional — иначе
     */
    Optional<Card> findByIdAndUser(Long id, User user);

    /**
     * Находит все карты с указанным статусом (например, ACTIVE, BLOCKED) с поддержкой пагинации.
     * Используется администратором для фильтрации.
     *
     * @param status   статус карт
     * @param pageable параметры пагинации
     * @return страница с картами указанного статуса
     */
    Page<Card> findByStatus(Status status, Pageable pageable);

    /**
     * Находит все карты в системе с поддержкой пагинации.
     * Используется администратором для просмотра всех карт.
     *
     * @param pageable параметры пагинации
     * @return страница со всеми картами
     */
    Page<Card> findAll(Pageable pageable);

    /**
     * Находит активную карту по ID, принадлежащую указанному пользователю.
     * Используется при переводе средств — перевод возможен только с активной карты.
     *
     * @param cardId ID карты
     * @param user   владелец карты
     * @return Optional с картой, если она активна и принадлежит пользователю; пустой Optional — иначе
     */
    @Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.user = :user AND c.status = 'ACTIVE'")
    Optional<Card> findActiveCardByIdAndUser(@Param("cardId") Long cardId, @Param("user") User user);
}

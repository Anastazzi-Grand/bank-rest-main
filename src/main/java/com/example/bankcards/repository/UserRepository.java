package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link User}.
 * Предоставляет методы для поиска, проверки существования и управления пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по уникальному имени (логину).
     *
     * @param username логин пользователя
     * @return Optional с пользователем, если найден; пустой Optional — если не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет, существует ли пользователь с указанным именем.
     *
     * @param username логин для проверки
     * @return true, если пользователь с таким именем уже существует; false — иначе
     */
    boolean existsByUsername(String username);
}
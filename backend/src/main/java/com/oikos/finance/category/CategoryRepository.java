package com.oikos.finance.category;

import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Todas las categorías de un usuario
    List<Category> findByUser(User user);

    // Una categoría concreta que pertenezca a un usuario (para autorización)
    Optional<Category> findByIdAndUser(UUID id, User user);

    // Comprobar si un usuario ya tiene una categoría con ese nombre
    boolean existsByUserAndName(User user, String name);
}
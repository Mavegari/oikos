package com.oikos.finance.budget;

import com.oikos.finance.category.Category;
import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    // Presupuestos de un usuario para un mes/año concretos
    List<Budget> findByUserAndMonthAndYear(User user, int month, int year);

    // Todos los del usuario
    List<Budget> findByUser(User user);

    // Uno concreto que pertenezca al usuario (autorización)
    Optional<Budget> findByIdAndUser(UUID id, User user);

    // Comprobar duplicado antes de crear (mismo user+categoría+mes+año)
    boolean existsByUserAndCategoryAndMonthAndYear(
            User user, Category category, int month, int year);
}
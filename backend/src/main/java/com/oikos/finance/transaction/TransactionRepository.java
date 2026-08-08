package com.oikos.finance.transaction;

import com.oikos.finance.account.Account;
import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.oikos.finance.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Todas las transacciones del usuario, más recientes primero
    List<Transaction> findByUserOrderByDateDesc(User user);

    // Una transacción concreta que pertenezca al usuario (autorización)
    Optional<Transaction> findByIdAndUser(UUID id, User user);

    // Suma de importes de una cuenta según el tipo (para el balance)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account = :account AND t.type = :type")
    BigDecimal sumAmountByAccountAndType(
            @Param("account") Account account,
            @Param("type") TransactionType type);

// Suma de gastos de una categoría dentro de un rango de fechas (para presupuestos)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.category = :category AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumAmountByCategoryAndTypeAndDateBetween(
            @Param("category") Category category,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

// Suma de ingresos o gastos del usuario en un rango de fechas (para totales del mes)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user = :user AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumAmountByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Gasto agrupado por categoría en un rango de fechas (para el gráfico de tarta)
    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user = :user AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate " +
           "GROUP BY t.category.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumAmountByCategoryGrouped(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
       
       // Todas las transacciones del usuario (sin filtro de fecha)
    List<Transaction> findByUser(User user);

    // Verificar si existe una transacción duplicada
    boolean existsByAccountAndCategoryAndAmountAndDate(
            Account account,
            Category category,
            BigDecimal amount,
            LocalDate date);
}
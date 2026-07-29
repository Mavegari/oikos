package com.oikos.finance.transaction;

import com.oikos.finance.account.Account;
import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
}
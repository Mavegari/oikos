package com.oikos.finance.debt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DebtPaymentRepository extends JpaRepository<DebtPayment, UUID> {
    
    List<DebtPayment> findByDebtOrderByDateDesc(Debt debt);
    
    @Query("SELECT COALESCE(SUM(dp.amount), 0) FROM DebtPayment dp WHERE dp.debt = :debt")
    BigDecimal sumAmountByDebt(@Param("debt") Debt debt);
}
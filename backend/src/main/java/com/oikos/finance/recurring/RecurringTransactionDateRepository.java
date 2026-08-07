package com.oikos.finance.recurring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RecurringTransactionDateRepository extends JpaRepository<RecurringTransactionDate, UUID> {
    
    List<RecurringTransactionDate> findByRecurringTransaction(RecurringTransaction recurringTransaction);
    
    @Query("SELECT rtd FROM RecurringTransactionDate rtd WHERE rtd.scheduledDate <= :date AND rtd.executed = false")
    List<RecurringTransactionDate> findAllByScheduledDateLessThanEqualAndExecutedFalse(
            @Param("date") LocalDate date
    );
}
package com.oikos.finance.recurring;

import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, UUID> {
    
    List<RecurringTransaction> findByUser(User user);
    
    Optional<RecurringTransaction> findByIdAndUser(UUID id, User user);
    
    @Query("SELECT rt FROM RecurringTransaction rt WHERE rt.user = :user AND rt.recurrenceMode = 'PATTERN' AND rt.active = true AND rt.nextRunDate <= :date")
    List<RecurringTransaction> findAllActivePatternByNextRunDateLessThanEqual(
            @Param("user") User user,
            @Param("date") LocalDate date
    );
}
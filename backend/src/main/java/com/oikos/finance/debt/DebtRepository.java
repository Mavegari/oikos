package com.oikos.finance.debt;

import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebtRepository extends JpaRepository<Debt, UUID> {
    
    List<Debt> findByUser(User user);
    
    Optional<Debt> findByIdAndUser(UUID id, User user);
}
package com.oikos.finance.investment;

import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestmentRepository extends JpaRepository<Investment, UUID> {
    
    List<Investment> findByUser(User user);
    
    Optional<Investment> findByIdAndUser(UUID id, User user);
}
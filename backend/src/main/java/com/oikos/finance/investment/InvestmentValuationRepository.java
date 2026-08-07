package com.oikos.finance.investment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InvestmentValuationRepository extends JpaRepository<InvestmentValuation, UUID> {
    
    List<InvestmentValuation> findByInvestmentOrderByDateDesc(Investment investment);
}
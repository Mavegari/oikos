package com.oikos.finance.investment;

import com.oikos.finance.investment.dto.InvestmentValuationRequest;
import com.oikos.finance.investment.dto.InvestmentValuationResponse;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class InvestmentValuationService {
    
    private final InvestmentRepository investmentRepository;
    private final InvestmentValuationRepository valuationRepository;
    
    public InvestmentValuationService(InvestmentRepository investmentRepository,
                                      InvestmentValuationRepository valuationRepository) {
        this.investmentRepository = investmentRepository;
        this.valuationRepository = valuationRepository;
    }
    
    @Transactional
    public InvestmentValuationResponse addValuation(UUID investmentId, InvestmentValuationRequest request, User user) {
        Investment investment = investmentRepository.findByIdAndUser(investmentId, user)
                .orElseThrow(() -> new IllegalArgumentException("Inversión no encontrada"));
        
        InvestmentValuation valuation = new InvestmentValuation(investment, request.value(), request.date());
        InvestmentValuation saved = valuationRepository.save(valuation);
        
        return toResponse(saved);
    }
    
    public List<InvestmentValuationResponse> getValuations(UUID investmentId, User user) {
        Investment investment = investmentRepository.findByIdAndUser(investmentId, user)
                .orElseThrow(() -> new IllegalArgumentException("Inversión no encontrada"));
        
        return valuationRepository.findByInvestmentOrderByDateDesc(investment)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional
    public void deleteValuation(UUID valuationId, UUID investmentId, User user) {
        Investment investment = investmentRepository.findByIdAndUser(investmentId, user)
                .orElseThrow(() -> new IllegalArgumentException("Inversión no encontrada"));
        
        InvestmentValuation valuation = valuationRepository.findById(valuationId)
                .orElseThrow(() -> new IllegalArgumentException("Valuación no encontrada"));
        
        if (!valuation.getInvestment().getId().equals(investment.getId())) {
            throw new IllegalArgumentException("La valuación no pertenece a esta inversión");
        }
        
        valuationRepository.delete(valuation);
    }
    
    private InvestmentValuationResponse toResponse(InvestmentValuation valuation) {
        return new InvestmentValuationResponse(
                valuation.getId(),
                valuation.getInvestment().getId(),
                valuation.getValue(),
                valuation.getDate(),
                valuation.getCreatedAt()
        );
    }
}
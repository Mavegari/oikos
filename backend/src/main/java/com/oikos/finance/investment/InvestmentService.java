package com.oikos.finance.investment;

import com.oikos.finance.investment.dto.InvestmentRequest;
import com.oikos.finance.investment.dto.InvestmentResponse;
import com.oikos.finance.investment.dto.InvestmentValuationDTO;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class InvestmentService {
    
    private final InvestmentRepository investmentRepository;
    private final InvestmentValuationRepository valuationRepository;
    
    public InvestmentService(InvestmentRepository investmentRepository,
                             InvestmentValuationRepository valuationRepository) {
        this.investmentRepository = investmentRepository;
        this.valuationRepository = valuationRepository;
    }
    
    @Transactional
    public InvestmentResponse create(InvestmentRequest request, User user) {
        Investment investment = new Investment(user, request.name(), request.investedAmount());
        Investment saved = investmentRepository.save(investment);
        return toResponse(saved);
    }
    
    public List<InvestmentResponse> findAll(User user) {
        return investmentRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public InvestmentResponse findById(UUID id, User user) {
        Investment investment = investmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Inversión no encontrada"));
        return toResponse(investment);
    }
    
    @Transactional
    public InvestmentResponse update(UUID id, InvestmentRequest request, User user) {
        Investment investment = investmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Inversión no encontrada"));
        
        investment.setName(request.name());
        investment.setInvestedAmount(request.investedAmount());
        
        Investment updated = investmentRepository.save(investment);
        return toResponse(updated);
    }
    
    @Transactional
    public void delete(UUID id, User user) {
        Investment investment = investmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Inversión no encontrada"));
        investmentRepository.delete(investment);
    }
    
    /**
     * Obtiene el valor actual (última valuación)
     */
    private BigDecimal getCurrentValue(Investment investment) {
        List<InvestmentValuation> valuations = valuationRepository.findByInvestmentOrderByDateDesc(investment);
        
        if (valuations.isEmpty()) {
            // Si no hay valuaciones, retornar el importe invertido
            return investment.getInvestedAmount();
        }
        
        return valuations.get(0).getValue();
    }
    
    /**
     * Calcula la ganancia/pérdida
     */
    private BigDecimal calculateProfitLoss(Investment investment, BigDecimal currentValue) {
        return currentValue.subtract(investment.getInvestedAmount());
    }
    
    /**
     * Calcula el porcentaje de ganancia/pérdida
     */
    private Double calculateProfitLossPercentage(BigDecimal investedAmount, BigDecimal profitLoss) {
        if (investedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        return profitLoss
                .divide(investedAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .doubleValue();
    }
    
    private InvestmentResponse toResponse(Investment investment) {
        BigDecimal currentValue = getCurrentValue(investment);
        BigDecimal profitLoss = calculateProfitLoss(investment, currentValue);
        Double profitLossPercentage = calculateProfitLossPercentage(investment.getInvestedAmount(), profitLoss);
        
        List<InvestmentValuationDTO> valuationDTOs = valuationRepository
                .findByInvestmentOrderByDateDesc(investment)
                .stream()
                .map(v -> new InvestmentValuationDTO(
                        v.getId(),
                        v.getValue(),
                        v.getDate().format(DateTimeFormatter.ISO_DATE)
                ))
                .toList();
        
        return new InvestmentResponse(
                investment.getId(),
                investment.getName(),
                investment.getInvestedAmount(),
                currentValue,
                profitLoss,
                profitLossPercentage,
                valuationDTOs,
                investment.getCreatedAt()
        );
    }
}
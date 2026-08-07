package com.oikos.finance.debt;

import com.oikos.finance.debt.dto.DebtRequest;
import com.oikos.finance.debt.dto.DebtResponse;
import com.oikos.finance.debt.dto.DebtPaymentDTO;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DebtService {
    
    private final DebtRepository debtRepository;
    private final DebtPaymentRepository paymentRepository;
    
    public DebtService(DebtRepository debtRepository, DebtPaymentRepository paymentRepository) {
        this.debtRepository = debtRepository;
        this.paymentRepository = paymentRepository;
    }
    
    @Transactional
    public DebtResponse create(DebtRequest request, User user) {
        Debt debt = new Debt(user, request.name(), request.totalAmount(), request.type());
        Debt saved = debtRepository.save(debt);
        return toResponse(saved);
    }
    
    public List<DebtResponse> findAll(User user) {
        return debtRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public DebtResponse findById(UUID id, User user) {
        Debt debt = debtRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));
        return toResponse(debt);
    }
    
    @Transactional
    public DebtResponse update(UUID id, DebtRequest request, User user) {
        Debt debt = debtRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));
        
        debt.setName(request.name());
        debt.setTotalAmount(request.totalAmount());
        debt.setType(request.type());
        
        Debt updated = debtRepository.save(debt);
        return toResponse(updated);
    }
    
    @Transactional
    public void delete(UUID id, User user) {
        Debt debt = debtRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));
        debtRepository.delete(debt);
    }
    
    /**
     * Calcula el total pagado
     */
    private BigDecimal calculatePaid(Debt debt) {
        return paymentRepository.sumAmountByDebt(debt);
    }
    
    /**
     * Calcula el pendiente por pagar
     */
    private BigDecimal calculatePending(Debt debt, BigDecimal paid) {
        return debt.getTotalAmount().subtract(paid);
    }
    
    /**
     * Calcula el porcentaje pendiente
     */
    private Double calculatePendingPercentage(BigDecimal totalAmount, BigDecimal pending) {
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        return pending
                .divide(totalAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .doubleValue();
    }
    
    private DebtResponse toResponse(Debt debt) {
        BigDecimal paid = calculatePaid(debt);
        BigDecimal pending = calculatePending(debt, paid);
        Double pendingPercentage = calculatePendingPercentage(debt.getTotalAmount(), pending);
        
        List<DebtPaymentDTO> paymentDTOs = paymentRepository
                .findByDebtOrderByDateDesc(debt)
                .stream()
                .map(p -> new DebtPaymentDTO(
                        p.getId(),
                        p.getAmount(),
                        p.getDate().format(DateTimeFormatter.ISO_DATE)
                ))
                .toList();
        
        return new DebtResponse(
                debt.getId(),
                debt.getName(),
                debt.getTotalAmount(),
                paid,
                pending,
                pendingPercentage,
                debt.getType(),
                paymentDTOs,
                debt.getCreatedAt()
        );
    }
}
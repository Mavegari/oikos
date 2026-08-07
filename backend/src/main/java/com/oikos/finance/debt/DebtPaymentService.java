package com.oikos.finance.debt;

import com.oikos.finance.debt.dto.DebtPaymentRequest;
import com.oikos.finance.debt.dto.DebtPaymentResponse;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class DebtPaymentService {
    
    private final DebtRepository debtRepository;
    private final DebtPaymentRepository paymentRepository;
    
    public DebtPaymentService(DebtRepository debtRepository, DebtPaymentRepository paymentRepository) {
        this.debtRepository = debtRepository;
        this.paymentRepository = paymentRepository;
    }
    
    @Transactional
    public DebtPaymentResponse addPayment(UUID debtId, DebtPaymentRequest request, User user) {
        Debt debt = debtRepository.findByIdAndUser(debtId, user)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));
        
        DebtPayment payment = new DebtPayment(debt, request.amount(), request.date());
        DebtPayment saved = paymentRepository.save(payment);
        
        return toResponse(saved);
    }
    
    public List<DebtPaymentResponse> getPayments(UUID debtId, User user) {
        Debt debt = debtRepository.findByIdAndUser(debtId, user)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));
        
        return paymentRepository.findByDebtOrderByDateDesc(debt)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional
    public void deletePayment(UUID paymentId, UUID debtId, User user) {
        Debt debt = debtRepository.findByIdAndUser(debtId, user)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));
        
        DebtPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        
        if (!payment.getDebt().getId().equals(debt.getId())) {
            throw new IllegalArgumentException("El pago no pertenece a esta deuda");
        }
        
        paymentRepository.delete(payment);
    }
    
    private DebtPaymentResponse toResponse(DebtPayment payment) {
        return new DebtPaymentResponse(
                payment.getId(),
                payment.getDebt().getId(),
                payment.getAmount(),
                payment.getDate(),
                payment.getCreatedAt()
        );
    }
}
package com.oikos.finance.debt;

import com.oikos.finance.debt.dto.DebtRequest;
import com.oikos.finance.debt.dto.DebtResponse;
import com.oikos.finance.debt.dto.DebtPaymentRequest;
import com.oikos.finance.debt.dto.DebtPaymentResponse;
import com.oikos.finance.user.User;
import com.oikos.finance.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/debts")
public class DebtController {
    
    private final DebtService debtService;
    private final DebtPaymentService paymentService;
    private final UserRepository userRepository;
    
    public DebtController(DebtService debtService, DebtPaymentService paymentService, UserRepository userRepository) {
        this.debtService = debtService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    
    @PostMapping
    public ResponseEntity<DebtResponse> create(
            @Valid @RequestBody DebtRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        DebtResponse response = debtService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<DebtResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(debtService.findAll(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DebtResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(debtService.findById(id, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DebtResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DebtRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(debtService.update(id, request, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        debtService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/payments")
    public ResponseEntity<DebtPaymentResponse> addPayment(
            @PathVariable UUID id,
            @Valid @RequestBody DebtPaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        DebtPaymentResponse response = paymentService.addPayment(id, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}/payments")
    public ResponseEntity<List<DebtPaymentResponse>> getPayments(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(paymentService.getPayments(id, user));
    }
    
    @DeleteMapping("/{debtId}/payments/{paymentId}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable UUID debtId,
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        paymentService.deletePayment(paymentId, debtId, user);
        return ResponseEntity.noContent().build();
    }
}
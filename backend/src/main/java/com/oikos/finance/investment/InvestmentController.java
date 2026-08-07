package com.oikos.finance.investment;

import com.oikos.finance.investment.dto.InvestmentRequest;
import com.oikos.finance.investment.dto.InvestmentResponse;
import com.oikos.finance.investment.dto.InvestmentValuationRequest;
import com.oikos.finance.investment.dto.InvestmentValuationResponse;
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
@RequestMapping("/api/investments")
public class InvestmentController {
    
    private final InvestmentService investmentService;
    private final InvestmentValuationService valuationService;
    private final UserRepository userRepository;
    
    public InvestmentController(InvestmentService investmentService,
                                InvestmentValuationService valuationService,
                                UserRepository userRepository) {
        this.investmentService = investmentService;
        this.valuationService = valuationService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    
    @PostMapping
    public ResponseEntity<InvestmentResponse> create(
            @Valid @RequestBody InvestmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        InvestmentResponse response = investmentService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<InvestmentResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(investmentService.findAll(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvestmentResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(investmentService.findById(id, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InvestmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody InvestmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(investmentService.update(id, request, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        investmentService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/valuations")
    public ResponseEntity<InvestmentValuationResponse> addValuation(
            @PathVariable UUID id,
            @Valid @RequestBody InvestmentValuationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        InvestmentValuationResponse response = valuationService.addValuation(id, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}/valuations")
    public ResponseEntity<List<InvestmentValuationResponse>> getValuations(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(valuationService.getValuations(id, user));
    }
    
    @DeleteMapping("/{investmentId}/valuations/{valuationId}")
    public ResponseEntity<Void> deleteValuation(
            @PathVariable UUID investmentId,
            @PathVariable UUID valuationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        valuationService.deleteValuation(valuationId, investmentId, user);
        return ResponseEntity.noContent().build();
    }
}
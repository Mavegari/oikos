package com.oikos.finance.recurring;

import com.oikos.finance.recurring.dto.RecurringTransactionRequest;
import com.oikos.finance.recurring.dto.RecurringTransactionResponse;
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
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {
    
    private final RecurringTransactionService recurringService;
    private final UserRepository userRepository;
    
    public RecurringTransactionController(RecurringTransactionService recurringService, UserRepository userRepository) {
        this.recurringService = recurringService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    
    @PostMapping
    public ResponseEntity<RecurringTransactionResponse> create(
            @Valid @RequestBody RecurringTransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        RecurringTransactionResponse response = recurringService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(recurringService.findAll(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(recurringService.findById(id, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RecurringTransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(recurringService.update(id, request, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        recurringService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
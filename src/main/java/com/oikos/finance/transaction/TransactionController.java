package com.oikos.finance.transaction;

import com.oikos.finance.transaction.dto.TransactionRequest;
import com.oikos.finance.transaction.dto.TransactionResponse;
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
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        TransactionResponse response = transactionService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(transactionService.findAll(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(transactionService.findById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(transactionService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        transactionService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
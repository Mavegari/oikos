package com.oikos.finance.budget;

import com.oikos.finance.budget.dto.BudgetRequest;
import com.oikos.finance.budget.dto.BudgetResponse;
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
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserRepository userRepository;

    public BudgetController(BudgetService budgetService, UserRepository userRepository) {
        this.budgetService = budgetService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        BudgetResponse response = budgetService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> findAll(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(budgetService.findAll(user, month, year));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(budgetService.findById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(budgetService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        budgetService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
package com.oikos.finance.transfer;

import com.oikos.finance.transfer.dto.TransferRequest;
import com.oikos.finance.transfer.dto.TransferResponse;
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
@RequestMapping("/api/transfers")
public class TransferController {
    
    private final TransferService transferService;
    private final UserRepository userRepository;
    
    public TransferController(TransferService transferService, UserRepository userRepository) {
        this.transferService = transferService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    
    @PostMapping
    public ResponseEntity<TransferResponse> create(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        TransferResponse response = transferService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TransferResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(transferService.findAll(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(transferService.findById(id, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TransferResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(transferService.update(id, request, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        transferService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
package com.oikos.finance.dashboard;

import com.oikos.finance.dashboard.dto.DashboardResponse;
import com.oikos.finance.user.User;
import com.oikos.finance.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.oikos.finance.dashboard.dto.DashboardSummary;
import com.oikos.finance.dashboard.dto.MonthlyDataPoint;
import java.util.List;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);

        // Si no se indica mes/año, usar el mes actual
        LocalDate now = LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        return ResponseEntity.ok(
                dashboardService.getSummary(user, targetMonth, targetYear));
    }

    /**
 * Obtiene resumen general de todas las transacciones (sin filtro de mes)
 */
    @GetMapping("/summary-all")
    public ResponseEntity<DashboardSummary> getSummaryAll(
        @AuthenticationPrincipal UserDetails userDetails) {
    User user = getCurrentUser(userDetails);
    return ResponseEntity.ok(dashboardService.getSummary(user));
    }

    /**
 * Obtiene serie mensual de ingresos, gastos y neto para un año específico
 */
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyDataPoint>> getMonthlySeries(
        @RequestParam(required = false) Integer year,
        @AuthenticationPrincipal UserDetails userDetails) {
    User user = getCurrentUser(userDetails);
    
    // Si no se indica año, usar el año actual
    LocalDate now = LocalDate.now();
    int targetYear = (year != null) ? year : now.getYear();
    
    return ResponseEntity.ok(dashboardService.getMonthlySeries(user, targetYear));
    }
}
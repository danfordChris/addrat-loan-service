package com.pesa.controller;

import com.pesa.dto.ApiResponse;
import com.pesa.dto.LoanApplicationRequest;
import com.pesa.dto.LoanCalculationResponse;
import com.pesa.dto.LoanListResponse;
import com.pesa.dto.LoanResponse;
import com.pesa.entity.Loan;
import com.pesa.mapper.LoanMapper;
import com.pesa.service.LoanEventStreamService;
import com.pesa.service.LoanService;
import com.pesa.util.LoanCalculator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);
    private final LoanService loanService;
    private final LoanEventStreamService loanEventStreamService;

    public LoanController(LoanService loanService, LoanEventStreamService loanEventStreamService) {
        this.loanService = loanService;
        this.loanEventStreamService = loanEventStreamService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProducts(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        List<Map<String, Object>> products = loanService.getEligibleProducts(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Products retrieved", products));
    }

    @GetMapping("/products/eligible")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEligibleProducts(Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        List<Map<String, Object>> products = loanService.getEligibleProducts(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Eligible products retrieved", products));
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<LoanCalculationResponse>> calculateLoan(
            @Valid @RequestBody LoanApplicationRequest request) {
        try {
            LoanCalculator.LoanBreakdown breakdown = loanService.calculateLoan(request.getAmount(),
                    request.getDurationMonths());
            LoanCalculationResponse response = LoanMapper.toLoanCalculationResponse(breakdown);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loan calculation successful", response));
        } catch (RuntimeException e) {
            log.error("Error calculating loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(
            @Valid @RequestBody LoanApplicationRequest request,
            Authentication authentication) {
        return submitLoanApplication(request, authentication);
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoanV2(
            @Valid @RequestBody LoanApplicationRequest request,
            Authentication authentication) {
        return submitLoanApplication(request, authentication);
    }

    private ResponseEntity<ApiResponse<LoanResponse>> submitLoanApplication(
            LoanApplicationRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            Loan loan = loanService.applyForLoan(userId, request.getAmount(), request.getDurationMonths());
            LoanResponse response = LoanMapper.toLoanResponse(loan);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loan application submitted", response));
        } catch (RuntimeException e) {
            log.error("Error applying for loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoan(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            Loan loan = loanService.getLoanForUser(loanId, userId);
            LoanResponse response = LoanMapper.toLoanResponse(loan);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loan retrieved", response));
        } catch (RuntimeException e) {
            log.error("Error retrieving loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping(path = "/{loanId}/events/stream", produces = "text/event-stream")
    public SseEmitter streamLoanEvents(
            @PathVariable Long loanId,
            Authentication authentication) {
        Long userId = (Long) authentication.getDetails();
        Loan loan = loanService.getLoanForUser(loanId, userId);
        return loanEventStreamService.subscribe(loanId, userId, loan);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<LoanResponse>> getActiveLoan(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            Loan loan = loanService.getActiveLoan(userId);
            if (loan == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "No active loan", null));
            }
            LoanResponse response = LoanMapper.toLoanResponse(loan);
            return ResponseEntity.ok(new ApiResponse<>(true, "Active loan retrieved", response));
        } catch (RuntimeException e) {
            log.error("Error retrieving active loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{loanId}/accept")
    public ResponseEntity<ApiResponse<LoanResponse>> acceptLoan(
            @PathVariable Long loanId,
            @RequestBody Map<String, String> body,
            Authentication authentication,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            Long userId = (Long) authentication.getDetails();
            String pin = body.get("pin");
            Loan loan = loanService.acceptLoan(userId, loanId, pin, authorization);
            LoanResponse response = LoanMapper.toLoanResponse(loan);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loan accepted", response));
        } catch (RuntimeException e) {
            log.error("Error accepting loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LoanListResponse>> getUserLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            Page<Loan> loans = loanService.getUserLoans(userId, PageRequest.of(page, size));
            LoanListResponse response = LoanMapper.toLoanListResponse(loans);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loans retrieved", response));
        } catch (RuntimeException e) {
            log.error("Error retrieving user loans: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.approveLoan(loanId);
            LoanResponse response = LoanMapper.toLoanResponse(loan);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loan approved", response));
        } catch (RuntimeException e) {
            log.error("Error approving loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{loanId}/disburse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> disburseLoan(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.disburseLoan(loanId);
            LoanResponse response = LoanMapper.toLoanResponse(loan);
            return ResponseEntity.ok(new ApiResponse<>(true, "Loan disbursed", response));
        } catch (RuntimeException e) {
            log.error("Error disbursing loan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

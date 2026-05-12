package com.pesa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "loan.policy")
@Data
public class LoanPolicyProperties {

    private BigDecimal applicationFeePct = new BigDecimal("5.0");
    private BigDecimal monthlyInterestRatePct = new BigDecimal("3.5");
    private BigDecimal monthlyPenaltyRatePct = new BigDecimal("5.0");

    private BigDecimal completedLoanStepUpOne = new BigDecimal("1.25");
    private BigDecimal completedLoanStepUpThree = new BigDecimal("1.50");

    private BigDecimal utilizationFactorMedium = new BigDecimal("0.75");
    private BigDecimal utilizationFactorHigh = new BigDecimal("0.50");

    private BigDecimal recommendedAmountRatio = new BigDecimal("0.60");

    private List<ProductPolicy> products = defaultProducts();

    @Data
    public static class ProductPolicy {
        private String id;
        private String code;
        private String name;
        private String category;
        private Integer minAmount;
        private Integer maxAmount;
        private Integer minTermDays;
        private Integer maxTermDays;
    }

    private static List<ProductPolicy> defaultProducts() {
        List<ProductPolicy> defaults = new ArrayList<>();
        defaults.add(product("instant", "INSTANT", "Mkopo wa Haraka", "instant", 50000, 500000, 7, 30));
        defaults.add(product("emergency", "EMERGENCY", "Mkopo wa Dharura", "emergency", 100000, 1500000, 14, 90));
        defaults.add(product("business", "BUSINESS", "Mkopo wa Biashara", "business", 300000, 5000000, 30, 360));
        return defaults;
    }

    private static ProductPolicy product(String id, String code, String name, String category,
                                         Integer minAmount, Integer maxAmount, Integer minTermDays, Integer maxTermDays) {
        ProductPolicy p = new ProductPolicy();
        p.setId(id);
        p.setCode(code);
        p.setName(name);
        p.setCategory(category);
        p.setMinAmount(minAmount);
        p.setMaxAmount(maxAmount);
        p.setMinTermDays(minTermDays);
        p.setMaxTermDays(maxTermDays);
        return p;
    }
}

package model;

import java.math.BigDecimal;

public class SystemConfig {

    private static SystemConfig instance;

    private BigDecimal interestRatePerDay;
    private BigDecimal fineRate;
    private int maxInstallments;
    private BigDecimal minPurchaseAmount;

    private SystemConfig() {
        this.interestRatePerDay = new BigDecimal("0.001");
        this.fineRate = new BigDecimal("0.02");
        this.maxInstallments = 6;
        this.minPurchaseAmount = new BigDecimal("10.00");
    }

    public static SystemConfig getInstance() {
        if (instance == null) {
            instance = new SystemConfig();
        }
        return instance;
    }

    public BigDecimal getInterestRatePerDay() {
        return interestRatePerDay;
    }

    public void setInterestRatePerDay(BigDecimal interestRatePerDay) {
        this.interestRatePerDay = interestRatePerDay;
    }

    public BigDecimal getFineRate() {
        return fineRate;
    }

    public void setFineRate(BigDecimal fineRate) {
        this.fineRate = fineRate;
    }

    public int getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(int maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public BigDecimal getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }
}

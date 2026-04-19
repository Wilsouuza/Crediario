package model;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private long id;
    private Installment installment;
    private LocalDate date;
    private BigDecimal originalAmount;
    private BigDecimal fineAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestAmount;
    private PaymentMethod paymentMethod;

    public Payment(Installment installment, LocalDate date, BigDecimal originalAmount, BigDecimal fineAmount, BigDecimal paidAmount,BigDecimal interestAmount ,PaymentMethod paymentMethod) {

        this.installment = installment;
        this.date = date;
        this.originalAmount = originalAmount;
        this.fineAmount = fineAmount;
        this.paidAmount = paidAmount;
        this.interestAmount = interestAmount;
        this.paymentMethod = paymentMethod;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Installment getInstallment() {
        return installment;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }
}

package model;

import enums.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Installment {
    private long id;
    private Purchase purchase;
    private BigDecimal value;
    private LocalDate dueDate;
    private InstallmentStatus status;

    public Installment(Purchase purchase, BigDecimal value, LocalDate dueDate) {
        this.purchase = purchase;
        this.value = value;
        this.dueDate = dueDate;
        this.status = InstallmentStatus.PENDING;
    }

    public long getId() {
        return id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }
}

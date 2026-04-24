package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Purchase {

    private long id;
    private Customer customer;
    private BigDecimal value;
    private LocalDate date;
    private int qtyInstallments;
    private List<Installment> installments = new ArrayList<>();
    private String description;

    public Purchase(Customer customer, BigDecimal value, LocalDate date, int qtyInstallments, String description) {
        this.customer = customer;
        this.value = value;
        this.date = date;
        this.qtyInstallments = qtyInstallments;
        this.description = description;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }


    public int getQtyInstallments() {
        return qtyInstallments;
    }


    public void addInstallment(Installment installment) {
        this.installments.add(installment);
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public String getDescription() {
        return description;
    }

}

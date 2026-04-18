package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Customer {

    private long id;
    private String name;
    private String cpf;
    private String phone;
    private String profession;
    private LocalDate birthDate;
    private BigDecimal creditLimit;

    public Customer(String name, String cpf, String phone, String profession, LocalDate birthDate, BigDecimal creditLimit) {
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.profession = profession;
        this.birthDate = birthDate;
        this.creditLimit = creditLimit;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}

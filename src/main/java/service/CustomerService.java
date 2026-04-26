package service;

import model.Customer;
import model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CustomerService {

    Customer createCustomer(String name, String cpf, String phone, String profession, LocalDate birthDate, User registeredBy);
    Customer findById(long id);
    Customer findByCpf (String cpf);
    List<Customer> findAll();
    List<Customer> findAllWithLateInstallments();
    void updateCustomer(String name, String cpf, String phone, String profession, LocalDate birthDate);
    BigDecimal getAvailableLimit(String cpf);
    boolean hasLateInstallments(String cpf);
}

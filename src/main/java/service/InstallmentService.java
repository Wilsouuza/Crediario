package service;

import enums.InstallmentStatus;
import model.Customer;
import model.Installment;
import model.Purchase;

import java.util.List;

public interface InstallmentService {
    void generateInstallments(Purchase purchase);
    void updateOverdueInstallments();
    boolean hasLateInstallments(Customer customer);
    List<Installment> findByPurchase(Purchase purchase);
    List<Installment> findByCustomer(Customer customer);
    List<Installment> findByStatus(InstallmentStatus status);
    List<Installment> findByCustomerAndStatus(Customer customer, InstallmentStatus status);
    List<Installment> findOpenInstallmentsByCustomer(Customer customer);
    Installment findById(long id);
    List<Installment> findAll();

}

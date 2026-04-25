package repository;

import enums.InstallmentStatus;
import model.Customer;
import model.Installment;
import model.Purchase;

import java.util.List;

public interface InstallmentRepository {
    void save(Installment installment);
    Installment findById(long id);
    List<Installment> findByPurchase(Purchase purchase);
    List<Installment> findByCustomer(Customer customer);
    List<Installment> findByStatus(InstallmentStatus status);
    List<Installment> findByCustomerAndStatus(Customer customer, InstallmentStatus status);
    List<Installment> findAll();
    void delete(long id);
}

package repository;

import model.Customer;
import model.Payment;
import model.Purchase;

import java.util.List;

public interface PaymentRepository {
    void save(Payment payment);
    Payment findById(long id);
    List<Payment> findByPurchase(Purchase purchase);
    List<Payment> findByCustomer(Customer customer);
    List<Payment> findAll();
    void delete(long id);
}

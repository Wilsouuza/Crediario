package repository;

import model.Customer;
import model.Payment;
import model.Purchase;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository {
    void save(Payment payment);
    Payment findById(long id);
    List<Payment> findByPurchase(Purchase purchase);
    List<Payment> findByCustomer(Customer customer);
    List<Payment> findAll();
    List<Payment> findByDateRange(LocalDate start, LocalDate end);
    void delete(long id);
}

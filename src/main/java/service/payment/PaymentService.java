package service.payment;

import enums.PaymentMethod;
import model.Customer;
import model.Installment;
import model.Payment;
import model.Purchase;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    void createPayment(Installment installment, PaymentMethod paymentMethod);
    Payment findById(long id);
    List<Payment> findByPurchase(Purchase purchase);
    List<Payment> findByCustomer(Customer customer);
    List<Payment> findByDateRange(LocalDate start, LocalDate end);
    List<Payment> findAll();
}

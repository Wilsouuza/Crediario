package service.purchase;

import model.Customer;
import model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    Purchase createPurchase(Customer customer, BigDecimal value, LocalDate date, int qtyInstallments, String description);
    Purchase findById(long id);
    List<Purchase> findByCustomer(Customer customer);
    List<Purchase> findAll();
}

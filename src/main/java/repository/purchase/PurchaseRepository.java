package repository.purchase;

import model.Customer;
import model.Purchase;

import java.util.List;

public interface PurchaseRepository {
    Purchase save(Purchase purchase);
    Purchase findById(long id);
    List<Purchase> findByCustomer(Customer customer);
    List<Purchase> findAll();
    void delete(long id);
}

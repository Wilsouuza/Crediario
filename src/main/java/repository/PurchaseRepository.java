package repository;

import model.Customer;
import model.Purchase;

import java.util.List;

public interface PurchaseRepository {
    void save(Purchase purchase);
    Purchase findById(long id);
    List<Purchase> findByCustomer(Customer customer);
    List<Purchase> findAll();
    void delete(long id);
}

package repository;

import model.Customer;
import model.Purchase;

import java.util.ArrayList;
import java.util.List;

public class PurchaseRepositoryImpl implements PurchaseRepository {
    private List<Purchase> purchases = new ArrayList<>();
    private long nextId = 1;

    public void save(Purchase purchase){
        purchase.setId(nextId++);
        purchases.add(purchase);
    }

    public Purchase findById(long id){
        for (Purchase p : purchases){
            if (p.getId() == id){
                return p;
            }
        }
        return null;
    }

    public List<Purchase> findByCustomer(Customer customer){
        List<Purchase> result = new ArrayList<>();
        for (Purchase p : purchases){
            if (p.getCustomer().getId() == customer.getId()){
                result.add(p);
            }
        }
        return result;
    }



    public List<Purchase> findAll(){
        return new ArrayList<>(purchases);
    }

    public void delete(long id){
        Purchase purchase = findById(id);
        if (purchase != null){
            purchases.remove(purchase);
        }
    }



}

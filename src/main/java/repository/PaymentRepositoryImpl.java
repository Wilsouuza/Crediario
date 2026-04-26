package repository;

import model.Customer;
import model.Payment;
import model.Purchase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepositoryImpl implements PaymentRepository {
    private List<Payment> payments = new ArrayList<>();
    private long nextId = 1;

    public void save(Payment payment){
        payment.setId(nextId++);
        payments.add(payment);
    }

    public Payment findById(long id){
        for (Payment p : payments){
            if (p.getId() == id){
                return p;
            }
        }
        return null;
    }

    public List<Payment> findByPurchase(Purchase purchase){
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments){
            if (p.getInstallment().getPurchase().getId() == purchase.getId()){
                result.add(p);
            }
        }
        return result;
    }

    public List<Payment> findByCustomer(Customer customer){
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments){
            if (p.getInstallment().getPurchase().getCustomer().getId() == customer.getId()){
                result.add(p);
            }
        }
        return result;
    }

    public List<Payment> findByDateRange(LocalDate start, LocalDate end){
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments){
            if (!p.getDate().toLocalDate().isBefore(start) && !p.getDate().toLocalDate().isAfter(end)) {
                result.add(p);
            }
        }
        return result;
    }


    public List<Payment> findAll(){
        return new ArrayList<>(payments);
    }

    public void delete(long id) {
        Payment payment = findById(id);
        if (payment != null) {
            payments.remove(payment);
        }
    }


}

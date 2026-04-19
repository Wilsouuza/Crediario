package repository;

import model.Installment;
import model.Purchase;

import java.util.ArrayList;
import java.util.List;

public class InstallmentRepository {
    private List<Installment> installments = new ArrayList<>();
    private long nextId = 1;

    public void save(Installment installment){
        installment.setId(nextId++);
        installments.add(installment);
    }

    public Installment findById(long id){
        for (Installment i : installments){
            if (i.getId() == id){
                return i;
            }
        }
        return null;
    }


    public List<Installment> findByPurchase(Purchase purchase){
        List<Installment> result = new ArrayList<>();
        for (Installment i : installments){
            if (i.getPurchase().getId() == purchase.getId()){
                result.add(i);
            }
        }
        return result;
    }

    public List<Installment> findAll(){
        return new ArrayList<>(installments);
    }

    public void delete(long id){
        Installment installment = findById(id);
        if (installment != null){
            installments.remove(installment);
        }
    }





}

package service;

import enums.InstallmentStatus;
import model.Customer;
import model.Installment;
import model.Purchase;
import repository.InstallmentRepository;
import util.ValidationUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class InstallmentService {

    private InstallmentRepository installmentRepository;


    public InstallmentService(InstallmentRepository installmentRepository) {
        this.installmentRepository = installmentRepository;
    }

    public void generateInstallments(Purchase purchase){
        BigDecimal installmentValue = purchase.getValue().divide(new BigDecimal(purchase.getQtyInstallments()),2, RoundingMode.HALF_UP);

        for (int i = 0; i < purchase.getQtyInstallments(); i++) {
            LocalDate dueDate = purchase.getDate().plusDays(30L * (i + 1));
            Installment installment = new Installment(purchase, installmentValue,dueDate);
            purchase.addInstallment(installment);
            installmentRepository.save(installment);
        }
    }

    public void updateOverdueInstallments(){
        List<Installment> installments = installmentRepository.findAll();
        for (Installment i : installments){
            if (i.getStatus() == InstallmentStatus.PENDING && i.getDueDate().isBefore(LocalDate.now())){
                i.setStatus(InstallmentStatus.LATE);
            }
        }

    }

    public boolean hasLateInstallments(Customer customer){
        ValidationUtils.notNull(customer, "Customer");
        List<Installment> result = installmentRepository.findByCustomer(customer);
        for (Installment i : result){
            if (i.getStatus() == InstallmentStatus.LATE){
                return true;
            }
        }
        return false;
    }

    public List<Installment> findByPurchase(Purchase purchase){
        return installmentRepository.findByPurchase(purchase);
    }

    public List<Installment> findByCustomer(Customer customer){
        return installmentRepository.findByCustomer(customer);
    }

    public List<Installment> findAll(){
        return installmentRepository.findAll();
    }

}

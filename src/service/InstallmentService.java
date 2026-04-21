package service;

import enums.InstallmentStatus;
import model.Customer;
import model.Installment;
import repository.InstallmentRepository;
import util.ValidationUtils;

import java.util.List;

public class InstallmentService {

    private InstallmentRepository installmentRepository;


    public InstallmentService(InstallmentRepository installmentRepository) {
        this.installmentRepository = installmentRepository;
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
}

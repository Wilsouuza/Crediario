package service;

import exception.BusinessException;
import model.Customer;
import model.Purchase;
import model.SystemConfig;
import repository.PurchaseRepository;
import util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PurchaseServiceImpl implements PurchaseService {

    private PurchaseRepository purchaseRepository;
    private CustomerService customerService;
    private InstallmentService installmentService;
    private UserService userService;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, CustomerService customerService, InstallmentService installmentService, UserService userService) {
        this.purchaseRepository = purchaseRepository;
        this.customerService = customerService;
        this.installmentService = installmentService;
        this.userService = userService;
    }

    public void createPurchase(Customer customer, BigDecimal value, LocalDate date, int qtyInstallments, String description){
        ValidationUtils.notNull(customer, "Customer");
        ValidationUtils.notNull(value, "Value");
        ValidationUtils.notNull(date, "Date");
        ValidationUtils.notNullOrBlank(description, "Description");

        SystemConfig systemConfig = SystemConfig.getInstance();


        if (qtyInstallments <= 0) {
            throw new BusinessException("Qty installments must be greater than zero.");
        }

        if (qtyInstallments > systemConfig.getMaxInstallments()) {
            throw new BusinessException("Max installments exceeded.");
        }

        boolean hasLateInstallments  = customerService.hasLateInstallments(customer.getCpf());

        if (hasLateInstallments){
            throw new BusinessException("Customer has late installments.");
        }

        if (value.compareTo(systemConfig.getMinPurchaseAmount()) < 0){
            throw new BusinessException("Value below the minimum purchase.");
        }


        BigDecimal availableLimit = customerService.getAvailableLimit(customer.getCpf());
        if (availableLimit.compareTo(value) < 0){
            throw new BusinessException("Insufficient limit.");
        }

        LocalDateTime registrationDate = customer.getRegistrationDate();
        LocalDateTime purchaseDateTime = date.atStartOfDay();

        long hoursBetween = ChronoUnit.HOURS.between(registrationDate,purchaseDateTime);

        List<Purchase> hasPurchase = purchaseRepository.findByCustomer(customer);
        if (hasPurchase.isEmpty() && hoursBetween <= 24){
           userService.addCommission(customer.getRegisteredBy().getId(), new BigDecimal("10.00"));
        }


        Purchase purchase = new Purchase(customer,value,date,qtyInstallments,description);
        purchaseRepository.save(purchase);
        installmentService.generateInstallments(purchase);
    }

    public Purchase findById(long id){
        return purchaseRepository.findById(id);
    }

    public List<Purchase> findByCustomer(Customer customer){
        return purchaseRepository.findByCustomer(customer);
    }

    public List<Purchase> findAll(){
        return purchaseRepository.findAll();
    }

}

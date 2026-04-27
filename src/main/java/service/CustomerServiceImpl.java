package service;

import enums.InstallmentStatus;
import exception.BusinessException;
import model.Customer;
import model.Installment;
import model.SystemConfig;
import model.User;
import repository.customer.CustomerRepository;
import util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private UserService userService;
    private InstallmentService installmentService;


    public CustomerServiceImpl(CustomerRepository customerRepository, UserService userService, InstallmentService installmentService){
        this.customerRepository = customerRepository;
        this.userService = userService;
        this.installmentService = installmentService;
    }

    public Customer createCustomer(String name, String cpf, String phone, String profession, LocalDate birthDate,User registeredBy){
        ValidationUtils.notNullOrBlank(cpf, "CPF");
        ValidationUtils.notNullOrBlank(name,"Name");
        ValidationUtils.notNullOrBlank(phone, "Phone");
        ValidationUtils.notNull(birthDate,"Birth date");
        ValidationUtils.notNullOrBlank(profession, "Profession");
        ValidationUtils.notNull(registeredBy ,"Registered BY");

        BigDecimal creditLimit = SystemConfig.getInstance().getDefaultCreditLimit();

        Customer customer = customerRepository.findByCpf(cpf);

        if (customer != null){
            throw new BusinessException("CPF already registered.");
        }

        Customer newCustomer = new Customer(name,cpf,phone,profession,birthDate,creditLimit,registeredBy);
        customerRepository.save(newCustomer);

        userService.addCommission(registeredBy.getId(), new BigDecimal("4.00"));

        return newCustomer;
    }

    public Customer findById(long id){
        Customer customer = customerRepository.findById(id);
        ValidationUtils.exists(customer,"Customer");
        return customer;
    }

    public Customer findByCpf (String cpf){
        ValidationUtils.notNullOrBlank(cpf, "CPF");
        Customer customer = customerRepository.findByCpf(cpf);
        ValidationUtils.exists(customer,"Customer");
        return customer;
    }

    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    public List<Customer> findAllWithLateInstallments(){
        List<Customer> customers = customerRepository.findAll();
        List<Customer> customersWithLateInstallments = new ArrayList<>();
        for (Customer c : customers){
            if (installmentService.hasLateInstallments(c)){
                customersWithLateInstallments.add(c);
            }
        }
        return customersWithLateInstallments;
    }

    public void updateCustomer(String name, String cpf, String phone, String profession, LocalDate birthDate){
        ValidationUtils.notNullOrBlank(cpf, "CPF");
        ValidationUtils.notNullOrBlank(name,"Name");
        ValidationUtils.notNullOrBlank(phone, "Phone");
        ValidationUtils.notNull(birthDate,"Birth date");
        ValidationUtils.notNullOrBlank(profession, "Profession");

        Customer customer = customerRepository.findByCpf(cpf);

        ValidationUtils.exists(customer,"Customer");

        customer.setName(name);
        customer.setPhone(phone);
        customer.setProfession(profession);
        customer.setBirthDate(birthDate);
    }

    public BigDecimal getAvailableLimit(String cpf){
        ValidationUtils.notNullOrBlank(cpf, "CPF");

        Customer customer = customerRepository.findByCpf(cpf);

        ValidationUtils.exists(customer,"Customer");

        List<Installment> pendingInstallment =  installmentService.findByCustomerAndStatus(customer, InstallmentStatus.PENDING);
        List<Installment> lateInstallment =  installmentService.findByCustomerAndStatus(customer, InstallmentStatus.LATE);

        BigDecimal totalDebt = BigDecimal.ZERO;

        for (Installment i : pendingInstallment) {
            totalDebt = totalDebt.add(i.getValue());
        }
        for (Installment i : lateInstallment) {
            totalDebt = totalDebt.add(i.getValue());
        }

        return customer.getCreditLimit().subtract(totalDebt);
    }

    public boolean hasLateInstallments(String cpf){
        ValidationUtils.notNullOrBlank(cpf, "CPF");
        Customer customer = customerRepository.findByCpf(cpf);
        return installmentService.hasLateInstallments(customer);
    }
}

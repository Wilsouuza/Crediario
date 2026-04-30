package config;

import repository.customer.CustomerRepository;
import repository.customer.CustomerRepositoryJdbc;
import repository.installment.InstallmentRepository;
import repository.installment.InstallmentRepositoryJdbc;
import repository.payment.PaymentRepository;
import repository.payment.PaymentRepositoryJdbc;
import repository.purchase.PurchaseRepository;
import repository.purchase.PurchaseRepositoryJdbc;
import repository.user.UserRepository;
import repository.user.UserRepositoryJdbc;
import service.customer.CustomerService;
import service.customer.CustomerServiceImpl;
import service.installment.InstallmentService;
import service.installment.InstallmentServiceImpl;
import service.payment.PaymentService;
import service.payment.PaymentServiceImpl;
import service.purchase.PurchaseService;
import service.purchase.PurchaseServiceImpl;
import service.user.UserService;
import service.user.UserServiceImpl;

public class AppContext {

    private static AppContext instance;

    private final UserRepository userRepository = new UserRepositoryJdbc();
    private final CustomerRepository customerRepository = new CustomerRepositoryJdbc(userRepository);
    private final PurchaseRepository purchaseRepository = new PurchaseRepositoryJdbc(customerRepository);
    private final InstallmentRepository installmentRepository = new InstallmentRepositoryJdbc(purchaseRepository);
    private final PaymentRepository paymentRepository = new PaymentRepositoryJdbc(installmentRepository);

    private final UserService userService = new UserServiceImpl(userRepository);
    private final InstallmentService installmentService = new InstallmentServiceImpl(installmentRepository);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository, userService, installmentService);
    private final PurchaseService purchaseService = new PurchaseServiceImpl(purchaseRepository, customerService, installmentService, userService);
    private final PaymentService paymentService = new PaymentServiceImpl(paymentRepository, installmentService);


    private AppContext(){
    }

    public UserService getUserService() {
        return userService;
    }

    public InstallmentService getInstallmentService() {
        return installmentService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public PurchaseService getPurchaseService() {
        return purchaseService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public static AppContext getInstance(){
        if (instance == null){
            instance = new AppContext();
        }
        return instance;
    }


}

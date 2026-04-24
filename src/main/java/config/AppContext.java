package config;

import repository.*;
import service.*;

public class AppContext {

    private static AppContext instance;

    private final UserRepository userRepository = new UserRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();
    private final InstallmentRepository installmentRepository = new InstallmentRepository();
    private final PaymentRepository paymentRepository = new PaymentRepository();

    private final UserService userService = new UserService(userRepository);
    private final InstallmentService installmentService = new InstallmentService(installmentRepository);
    private final CustomerService customerService = new CustomerService(customerRepository, userService, installmentService);
    private final PurchaseService purchaseService = new PurchaseService(purchaseRepository, customerService, installmentService, userService);
    private final PaymentService paymentService = new PaymentService(paymentRepository, installmentService);

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

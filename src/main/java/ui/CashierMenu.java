package ui;

import config.AppContext;
import enums.InstallmentStatus;
import enums.PaymentMethod;
import exception.BusinessException;
import model.Installment;
import model.Payment;
import model.SystemConfig;
import model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class CashierMenu {
    private final AppContext context;
    private final Scanner scanner;
    private final User loggedUser;

    public CashierMenu(AppContext context, Scanner scanner, User loggedUser) {
        this.context = context;
        this.scanner = scanner;
        this.loggedUser = loggedUser;
    }

    public void start(){
        boolean running = true;
        while (running){
            System.out.println("\n=== Cashier Menu ===");
            System.out.println("1 - Register customer");
            System.out.println("2 - View customer balance");
            System.out.println("3 - View customer installments");
            System.out.println("4 - Receive payment");
            System.out.println("5 - Print statement");
            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){
                case "1" -> registerCustomer();
                case "2" -> viewCustomerBalance();
                case "3" -> viewCustomerInstallments();
                case "4" -> receivePayment();
                case "5" -> printStatement();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }

        }
    }

    private void registerCustomer(){
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Profession: ");
        String profession = scanner.nextLine();
        System.out.print("Birth Date: ");
        String birthDate = scanner.nextLine();

        try {
            context.getCustomerService().createCustomer(name,cpf,phone,profession, LocalDate.parse(birthDate),loggedUser );
            System.out.println("Customer registered successfully");
        } catch (BusinessException e){
            System.out.println("Error "+ e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void viewCustomerBalance(){
        System.out.print("\n=== View customer balance ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        try {
            BigDecimal balance = context.getCustomerService().getAvailableLimit(cpf);
            System.out.println("Balance: " + balance);
        } catch (BusinessException e){
            System.out.println("Error "+ e.getMessage());
        }
    }

    private void viewCustomerInstallments(){
        System.out.print("\n=== View customer Installments ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        try {
            List<Installment> installments = context.getInstallmentService().findByCustomer(context.getCustomerService().findByCpf(cpf));
            for (Installment i : installments){
                System.out.println("Purchase ID: " + i.getPurchase().getId());
                System.out.println("Installment ID" + i.getId());
                System.out.println("Value: " + i.getValue());
                System.out.println("\nDue Date: " + i.getDueDate());
                System.out.println("Status: " + i.getStatus().getDescription());
            }
        }catch (BusinessException e){
            System.out.println("Error " +  e.getMessage());
        }
    }

    private void viewCustomerInstallments(String cpf){
        try {
            List<Installment> installments = context.getInstallmentService().findByCustomer(context.getCustomerService().findByCpf(cpf));
            for (Installment i : installments){
                System.out.println("Purchase ID: " + i.getPurchase().getId());
                System.out.println("Installment ID" + i.getId());
                System.out.println("Value: " + i.getValue());
                System.out.println("\nDue Date: " + i.getDueDate());
                System.out.println("Status: " + i.getStatus().getDescription());
            }
        }catch (BusinessException e){
            System.out.println("Error " +  e.getMessage());
        }
    }

    private void receivePayment(){
        System.out.print("\n=== Payment ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        viewCustomerInstallments(cpf);
        System.out.print("Type the Installment Id for pay: ");
        long installmentId = Long.parseLong(scanner.nextLine());
        System.out.print("\n=== Payment Method ===");
        System.out.print("[1] Pix");
        System.out.print("[2] Cash");
        String op = scanner.nextLine();
        PaymentMethod paymentMethod = null;

        switch (op){
            case "1" -> paymentMethod = PaymentMethod.PIX;
            case "2" -> paymentMethod = PaymentMethod.CASH;
            default -> System.out.println("Invalid option");
        }
        if (paymentMethod == null) {
            System.out.println("Invalid payment method.");
            return;
        }
        try {
            context.getPaymentService().createPayment(context.getInstallmentService().findById(installmentId),paymentMethod);
            System.out.println("Payment made successfully");
        }catch (BusinessException e){
            System.out.println("Error " + e.getMessage());
        }
    }

    private void printStatement(){
        System.out.print("\n=== Print Statement ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        try {
            List<Payment> payments =  context.getPaymentService().findByCustomer(context.getCustomerService().findByCpf(cpf));
            for (Payment p : payments){
                System.out.println("Payment ID" + p.getId());
                System.out.println("Installment ID: " + p.getInstallment().getId());
                System.out.println("Date: " + p.getDate());
                System.out.println("Original amount: " + p.getOriginalAmount());
                System.out.println("Fine amount: " + p.getFineAmount());
                System.out.println("Paid amount: " + p.getPaidAmount());
                System.out.println("Interest amount: " + p.getInterestAmount());
                System.out.println("\nPayment method: " + p.getPaymentMethod());
            }
        }catch (BusinessException e){
            System.out.println("Error " + e.getMessage());
        }

    }
}

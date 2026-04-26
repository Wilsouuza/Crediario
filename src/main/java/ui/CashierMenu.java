package ui;

import config.AppContext;
import enums.InstallmentStatus;
import enums.PaymentMethod;
import exception.BusinessException;
import model.*;

import javax.swing.text.html.Option;
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
            System.out.println("3 - Register Purchase");
            System.out.println("4 - View customer installments");
            System.out.println("5 - Receive payment");
            System.out.println("6 - Print statement");
            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){
                case "1" -> registerCustomer();
                case "2" -> viewCustomerBalance();
                case "3" -> registerPurchase();
                case "4" -> viewCustomerInstallments();
                case "5" -> receivePayment();
                case "6" -> printStatement();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }

        }
    }

    private void registerCustomer(){
        System.out.println("\n=== Register customer ===");
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
        System.out.println("\n=== View customer balance ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        try {
            BigDecimal balance = context.getCustomerService().getAvailableLimit(cpf);
            System.out.println("Name: " + context.getCustomerService().findByCpf(cpf).getName());
            System.out.println("Balance: " + balance);
        } catch (BusinessException e){
            System.out.println("Error "+ e.getMessage());
        }
    }

    private void registerPurchase(){
        System.out.println("\n=== Register Purchase ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Purchase Value: ");
        BigDecimal purchaseValue = new BigDecimal(scanner.nextLine());
        System.out.print("Qty Installments: ");
        int qtyInstallments = Integer.parseInt(scanner.nextLine()) ;
        System.out.print("Description: ");
        String description = scanner.nextLine();

        try {
            Customer customer = context.getCustomerService().findByCpf(cpf);
            context.getPurchaseService().createPurchase(customer,purchaseValue,LocalDate.now(),qtyInstallments, description);
            System.out.println("Purchase registered successfully!");
        }catch (BusinessException e){
            System.out.println("Error " + e.getMessage());
        }
    }

    private void viewCustomerInstallments(){
        System.out.println("\n=== View customer Installments ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        try {
            context.getInstallmentService().updateOverdueInstallments();
            List<Installment> openInstallments = context.getInstallmentService().findOpenInstallmentsByCustomer(context.getCustomerService().findByCpf(cpf));
            for (Installment i : openInstallments){
                System.out.println("\nPurchase ID: " + i.getPurchase().getId());
                System.out.println("Installment ID" + i.getId());
                System.out.println("Value: " + i.getValue());
                System.out.println("Due Date: " + i.getDueDate());
                System.out.println("Status: " + i.getStatus().getDescription());
            }
        }catch (BusinessException e){
            System.out.println("Error " +  e.getMessage());
        }
    }

    private void viewCustomerInstallments(String cpf){
        try {
            context.getInstallmentService().updateOverdueInstallments();
            List<Installment> openInstallments = context.getInstallmentService().findOpenInstallmentsByCustomer(context.getCustomerService().findByCpf(cpf));
            for (Installment i : openInstallments){
                System.out.println("\nPurchase ID: " + i.getPurchase().getId());
                System.out.println("Installment ID" + i.getId());
                System.out.println("Value: " + i.getValue());
                System.out.println("Due Date: " + i.getDueDate());
                System.out.println("Status: " + i.getStatus().getDescription());
            }
        }catch (BusinessException e){
            System.out.println("Error " +  e.getMessage());
        }
    }

    private void receivePayment(){
        System.out.println("\n=== Payment ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        viewCustomerInstallments(cpf);
        System.out.print("Type the Installment Id for pay: ");
        long installmentId = Long.parseLong(scanner.nextLine());
        System.out.println("\nPayment Method");
        System.out.print("[1] Pix");
        System.out.println("[2] Cash");
        System.out.print("Option: ");
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
        System.out.println("\n=== Print Statement ===");
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

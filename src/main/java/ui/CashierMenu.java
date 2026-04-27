package ui;

import config.AppContext;
import enums.InstallmentStatus;
import enums.PaymentMethod;
import exception.BusinessException;
import model.*;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        System.out.print("Birth Date (dd/MM/yyyy): ");
        String birthDateStr = scanner.nextLine();

        LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try {
            context.getCustomerService().createCustomer(name,cpf,phone,profession, birthDate,loggedUser );
            System.out.println("Customer registered successfully");
        } catch (BusinessException e){
            System.out.println("Error "+ e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
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

            int counter = 1;
            long currentPurchaseId = -1;

            for (Installment i : openInstallments) {
                // se mudou de compra, exibe o cabeçalho da compra
                if (i.getPurchase().getId() != currentPurchaseId) {
                    currentPurchaseId = i.getPurchase().getId();
                    System.out.println("\nPurchase #" + i.getPurchase().getId()
                            + " — " + i.getPurchase().getDescription()
                            + " — R$" + i.getPurchase().getValue());
                }
                // exibe a parcela com número sequencial
                System.out.println("  " + counter + " - Vence: " + i.getDueDate()
                        + " | R$" + i.getValue()
                        + " | " + i.getStatus().getDescription());
                counter++;
            }

        }catch (BusinessException e){
            System.out.println("Error " +  e.getMessage());
        }
    }


    private void receivePayment(){
        System.out.println("\n=== Payment ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        context.getInstallmentService().updateOverdueInstallments();

        List<Installment> openInstallments = context.getInstallmentService().findOpenInstallmentsByCustomer(context.getCustomerService().findByCpf(cpf));
        openInstallments.sort((a, b) ->
                Long.compare(a.getPurchase().getId(), b.getPurchase().getId())
        );
        int counter = 1;
        long currentPurchaseId = -1;

        for (Installment i : openInstallments) {
            // se mudou de compra, exibe o cabeçalho da compra
            if (i.getPurchase().getId() != currentPurchaseId) {
                currentPurchaseId = i.getPurchase().getId();
                System.out.println("\nPurchase #" + i.getPurchase().getId()
                        + " — " + i.getPurchase().getDescription()
                        + " — R$" + i.getPurchase().getValue());
            }
            // exibe a parcela com número sequencial
            System.out.println("  " + counter + " - Vence: " + i.getDueDate()
                    + " | R$" + i.getValue()
                    + " | " + i.getStatus().getDescription());
            counter++;
        }
        System.out.print("Choose installment number: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;
        Installment selected = openInstallments.get(choice);


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
            context.getPaymentService().createPayment(selected,paymentMethod);
            System.out.println("Payment successfully.");
        }catch (BusinessException e){
            System.out.println("Error " + e.getMessage());
        }


    }

    private void printStatement(){
        System.out.println("\n=== Print Statement ===");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        try {
            List<Payment> payments = context.getPaymentService()
                    .findByCustomer(context.getCustomerService().findByCpf(cpf));

            if (payments.isEmpty()) {
                System.out.println("No payments found.");
                return;
            }

            System.out.println("\n--- Payment History ---");
            for (Payment p : payments) {
                System.out.println("\nPayment #" + p.getId()
                        + " | " + p.getDate().toLocalDate()
                        + " | " + p.getPaymentMethod().getDescription());
                System.out.println("  Installment #" + p.getInstallment().getId()
                        + " | Purchase: " + p.getInstallment().getPurchase().getDescription());
                System.out.println("  Original:  R$" + p.getOriginalAmount());
                System.out.println("  Fine:      R$" + p.getFineAmount());
                System.out.println("  Interest:  R$" + p.getInterestAmount());
                System.out.println("  Total:     R$" + p.getPaidAmount());
                System.out.println("  " + "-".repeat(40));
            }
        } catch (BusinessException e) {
            System.out.println("Error " + e.getMessage());
        }

    }
}

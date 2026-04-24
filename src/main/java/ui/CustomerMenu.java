package ui;

import config.AppContext;
import exception.BusinessException;
import model.Installment;
import model.Payment;
import model.Purchase;
import model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private AppContext context;
    private Scanner scanner;
    private User loggedUser;

    public CustomerMenu(AppContext context, Scanner scanner, User loggedUser) {
        this.context = context;
        this.scanner = scanner;
        this.loggedUser = loggedUser;
    }

    public void start(){
        boolean running = true;
        while (running){
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1 - View Installments");
            System.out.println("2 - View Purchase History");
            System.out.println("3 - View Available Credit Limit ");
            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){
                case "1" -> viewInstallments();
                case "2" -> viewPurchases();
                case "3" -> viewBalance();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }

        }
    }

    private void viewInstallments(){
        System.out.print("\n=== View Installments ===");
        try {
            List<Installment> installments = context.getInstallmentService().findByCustomer(context.getCustomerService().findByCpf(loggedUser.getCustomer().getCpf()));
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

    private void viewPurchases(){
        System.out.print("\n=== View Purchase History ===");
        try {
            List<Purchase> purchases = context.getPurchaseService().findByCustomer(loggedUser.getCustomer());
            for (Purchase p : purchases){
                System.out.println("Purchase ID" + p.getId());
                System.out.println("Value: " + p.getValue());
                System.out.println("Date: " + p.getDate());
                System.out.println("\nQty Installments: " + p.getQtyInstallments());
                List<Installment> installments = context.getInstallmentService().findByPurchase(p);
                System.out.print("\n=== Installments ===");
                for (Installment i : installments){

                    System.out.println("Purchase ID: " + i.getPurchase().getId());
                    System.out.println("Installment ID" + i.getId());
                    System.out.println("Value: " + i.getValue());
                    System.out.println("Due Date: " + i.getDueDate());
                    System.out.println("\nStatus: " + i.getStatus().getDescription());
                }
                System.out.println("\nDescription: " + p.getDescription());
            }
        }catch (BusinessException e){
            System.out.println("Erro " + e.getMessage());
        }

    }

    private void viewBalance(){
        System.out.print("\n=== View customer balance ===");
        try {
            BigDecimal balance = context.getCustomerService().getAvailableLimit(String.valueOf(loggedUser.getCustomer().getCpf()));
            System.out.println("Balance: " + balance);
        } catch (BusinessException e){
            System.out.println("Error "+ e.getMessage());
        }
    }
}

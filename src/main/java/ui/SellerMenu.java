package ui;

import config.AppContext;
import exception.BusinessException;
import model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class SellerMenu {
    private AppContext context;
    private Scanner scanner;
    private User loggedUser;


    public SellerMenu(AppContext context, Scanner scanner, User loggedUser) {
        this.context = context;
        this.scanner = scanner;
        this.loggedUser = loggedUser;
    }

    public void start(){
        boolean running = true;
        while (running){
            System.out.println("\n=== Seller Menu ===");
            System.out.println("1 - Register customer");
            System.out.println("2 - View customer balance");
            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){
                case "1" -> registerCustomer();
                case "2" -> viewCustomerBalance();
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
}

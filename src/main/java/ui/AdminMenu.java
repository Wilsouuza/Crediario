package ui;

import config.AppContext;
import enums.InstallmentStatus;
import enums.UserType;
import exception.BusinessException;
import model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final AppContext context;
    private final Scanner scanner;
    private final User loggedUser;

    public AdminMenu(AppContext context, Scanner scanner, User loggedUser) {
        this.context = context;
        this.scanner = scanner;
        this.loggedUser = loggedUser;
    }

    public void start(){
        boolean running = true;
        while (running){
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1 - Manage users");
            System.out.println("2 - View commissions");
            System.out.println("3 - System settings");
            System.out.println("4 - Reports");
            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){

                case "1" -> manageUsersMenu();
                case "2" -> viewCommissions();
                case "3" -> systemSettingsMenu();
                case "4" -> reports();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }

        }
    }

    private void manageUsersMenu(){
        boolean running = true;
        while (running){
            System.out.println("\n=== Manage Users ===");
            System.out.println("1 - Create user");
            System.out.println("2 - Delete user");
            System.out.println("3 - Change password");

            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){

                case "1" -> createUser();
                case "2" -> deleteUser();
                case "3" -> changePassword();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void createUser(){
        System.out.println("\n=== Creat User ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.println("\n=== User type ===");
        System.out.println("1 - Cashier");
        System.out.println("2 - Seller");
        System.out.println("3 - Admin");
        System.out.print("Option: ");
        UserType userType = null;

        String option = scanner.nextLine();

        switch (option){
            case "1" -> userType = UserType.CASHIER;
            case "2" -> userType = UserType.SELLER;
            case "3" -> userType = UserType.ADMIN;
            default -> System.out.println("Invalid option");
        }

        try {
            context.getUserService().createUser(login,password,userType);
            System.out.println("User created successfully.");
        } catch (BusinessException e){
            System.out.println("Error "+ e.getMessage());
        }
    }

    private void deleteUser(){
        System.out.println("\n=== Delete User ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();

        try {
            context.getUserService().deleteUser(login);
            System.out.println("User deleted successfully.");
        } catch (BusinessException e){
            System.out.println("Error " + e.getMessage());
        }
    }

    private void changePassword(){
        System.out.println("\n=== Change Password ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();

        try {
            context.getUserService().updatePassword(login, newPassword);
            System.out.println("Password change successfully.");
        } catch (BusinessException e){
            System.out.println("Error " + e.getMessage());
        }

    }

    private void viewCommissions(){
        List<User> users = context.getUserService().findAll();

        System.out.println("\n=== View Commissions ===");

        for (User u : users){
            if (u.getCommission().compareTo(BigDecimal.ZERO) > 0){
                System.out.println(u.getLogin());
                System.out.println(u.getCommission());
                System.out.println();
            }
        }
    }

    private void systemSettingsMenu(){
        boolean running = true;
        while (running){
            System.out.println("\n=== System Settings ===");
            System.out.println("1 - Change interest rate");
            System.out.println("2 - Change fine rate");
            System.out.println("3 - Change max installments");
            System.out.println("4 - Change min purchase amount");
            System.out.println("5 - Change default credit limit");
            System.out.println("0 - Logout");
            System.out.print("Option: ");

            String option = scanner.nextLine();

            switch (option){

                case "1" -> changeInterestRate();
                case "2" -> changeFineRate();
                case "3" -> changeMaxInstallments();
                case "4" -> changeMinPurchaseAmount();
                case "5" -> changeDefaultCreditLimit();
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }

        }
    }

    private void changeInterestRate(){
        System.out.println("\n=== Change Interest Rate ===");
        System.out.println("New interest rate: ");
        BigDecimal newInterestRate = new BigDecimal(scanner.nextLine());

        SystemConfig systemConfig = SystemConfig.getInstance();
        systemConfig.setInterestRatePerDay(newInterestRate);
    }

    private void changeFineRate(){
        System.out.println("\n=== Change Fine Rate ===");
        System.out.println("New fine rate: ");
        BigDecimal newFineRate = new BigDecimal(scanner.nextLine());

        SystemConfig systemConfig = SystemConfig.getInstance();
        systemConfig.setFineRate(newFineRate);
    }

    private void changeMaxInstallments(){
        System.out.println("\n=== Change Max Installments ===");
        System.out.println("New max installments: ");
        int newMaxInstallments = Integer.parseInt(scanner.nextLine());

        SystemConfig systemConfig = SystemConfig.getInstance();
        systemConfig.setMaxInstallments(newMaxInstallments);
    }

    private void changeMinPurchaseAmount(){
        System.out.println("\n=== Change Min Purchase Amount ===");
        System.out.println("New min purchase amount: ");
        BigDecimal newMinPurchaseAmount = new BigDecimal(scanner.nextLine());

        SystemConfig systemConfig = SystemConfig.getInstance();
        systemConfig.setMinPurchaseAmount(newMinPurchaseAmount);
    }

    private void changeDefaultCreditLimit(){
        System.out.println("\n=== Change Default CreditL Limit ===");
        System.out.println("New default creditL limit: ");
        BigDecimal newDefaultCreditLimit = new BigDecimal(scanner.nextLine());

        SystemConfig systemConfig = SystemConfig.getInstance();
        systemConfig.setDefaultCreditLimit(newDefaultCreditLimit);
    }

    private void reports(){

        //Sells
        List<Purchase> purchases = context.getPurchaseService().findAll();
        int totalPurchases = purchases.size();
        BigDecimal totalValuePurchases = BigDecimal.ZERO;
        for (Purchase p : purchases){
            totalValuePurchases = totalValuePurchases.add(p.getValue());
        }

        //default
        List<Customer> owingCustomers = context.getCustomerService().findAllWithLateInstallments();

        List<Installment> lateInstallments = context.getInstallmentService().findByStatus(InstallmentStatus.LATE);

        int totalOwingCustomers = owingCustomers.size();

        BigDecimal amountOfDebts = BigDecimal.ZERO;
        for (Installment i : lateInstallments){
           amountOfDebts = amountOfDebts.add(i.getValue());
        }

        System.out.println("\n=== Reports ===");
        System.out.print("Number of Purchases: " + totalPurchases);
        System.out.println("Amount of Purchases: " + totalValuePurchases);
        System.out.println("Number of Owing Customers: " + totalOwingCustomers);
        System.out.println("Amount of Debts: " + amountOfDebts);

    }
}
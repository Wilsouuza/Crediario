package ui;

import config.AppContext;
import exception.BusinessException;
import model.User;


import java.util.Scanner;

public class MainMenu {
    private AppContext context;
    private final Scanner scanner;

    public MainMenu(AppContext context,Scanner scanner){
        this.context = context;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("=== Crediário ===");
            System.out.println("0 - Exit");
            System.out.print("Login: ");
            String login = scanner.nextLine();

            if (login.equals("0")) {
                System.out.println("Goodbye!");
                running = false;
                continue;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                User user = context.getUserService().login(login, password);
                switch (user.getUserType()) {
                    case CUSTOMER -> new CustomerMenu(context, scanner, user).start();
                    case SELLER   -> new SellerMenu(context, scanner, user).start();
                    case CASHIER  -> new CashierMenu(context, scanner, user).start();
                    case ADMIN    -> new AdminMenu(context, scanner, user).start();
                }
            } catch (BusinessException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

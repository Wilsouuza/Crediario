import config.AppContext;
import ui.MainMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new MainMenu(AppContext.getInstance(), scanner).start();
    }
}

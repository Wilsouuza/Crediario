package enums;

public enum UserType {
    CUSTOMER("Cliente"),
    CASHIER("Caixa"),
    SELLER("Vendedor"),
    ADMIN("administrador");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

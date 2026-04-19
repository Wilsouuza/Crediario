package enums;

public enum InstallmentStatus {
    PAID("Pago"),
    PENDING("Pendente"),
    LATE("Atrasado");

    private final String description;

    InstallmentStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}

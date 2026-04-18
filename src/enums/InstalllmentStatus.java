package enums;

public enum InstalllmentStatus {
    PAID("Pago"),
    PENDING("Pendente"),
    LATE("Atrasado");

    private final String description;

    InstalllmentStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}

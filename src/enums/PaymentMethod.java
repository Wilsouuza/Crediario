package enums;

public enum PaymentMethod {
        PIX("Pix"),
        CASH("Dinheiro");

        private final String description;

        PaymentMethod(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
}

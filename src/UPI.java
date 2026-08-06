public class UPI extends PaymentMethod {
    @Override
    public boolean processPayment() {
        System.out.printf("Processing $%.2f via UPI...%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }
}

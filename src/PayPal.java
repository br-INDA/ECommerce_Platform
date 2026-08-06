public class PayPal extends PaymentMethod {
    @Override
    public boolean processPayment() {
        System.out.printf("Processing $%.2f via PayPal...%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "PayPal";
    }
}

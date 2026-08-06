public class CreditCard extends PaymentMethod {
    @Override
    public boolean processPayment() {
        System.out.printf("Processing $%.2f via Credit Card...%n", amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Credit Card";
    }
}

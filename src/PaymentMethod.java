/**
 * Base type for all payment strategies. Each subclass simulates a
 * different gateway; none of this talks to a real payment network.
 */
public abstract class PaymentMethod {
    protected double amount;

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public abstract boolean processPayment();

    public abstract String getMethodName();
}

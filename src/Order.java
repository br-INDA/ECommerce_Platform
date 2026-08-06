import java.time.LocalDateTime;
import java.util.List;

/**
 * A completed purchase: a snapshot of cart items at checkout time,
 * plus the payment method used and when it happened.
 */
public class Order {
    private String orderId;
    private Buyer buyer;
    private List<CartItem> items;
    private double totalAmount;
    private String paymentMethodName;
    private LocalDateTime orderDate;
    private OrderStatus status;

    public Order(String orderId, Buyer buyer, List<CartItem> items,
                 double totalAmount, String paymentMethodName) {
        this.orderId = orderId;
        this.buyer = buyer;
        this.items = items;
        this.totalAmount = totalAmount;
        this.paymentMethodName = paymentMethodName;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.CONFIRMED;
    }

    public String getOrderId() {
        return orderId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}

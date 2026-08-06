import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A shopper account. Adds three things the original Buyer class didn't have:
 * quantities in the cart (not just one-of-each), a wishlist, and order history.
 */
public class Buyer extends User {
    private String address;
    private Map<Product, Integer> cart = new LinkedHashMap<>();
    private List<Product> wishlist = new ArrayList<>();
    private List<Order> orderHistory = new ArrayList<>();

    public Buyer(String userId, String name, String email, String address) {
        super(userId, name, email);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    // ---- Cart ----

    public void addToCart(Product product, int quantity) {
        cart.merge(product, quantity, Integer::sum);
    }

    public void updateCartQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            cart.remove(product);
        } else {
            cart.put(product, quantity);
        }
    }

    public void removeFromCart(Product product) {
        cart.remove(product);
    }

    public double getCartTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public Map<Product, Integer> getCart() {
        return cart;
    }

    public void clearCart() {
        cart.clear();
    }

    // ---- Wishlist ----

    public void addToWishlist(Product product) {
        if (!wishlist.contains(product)) {
            wishlist.add(product);
        }
    }

    public void removeFromWishlist(Product product) {
        wishlist.remove(product);
    }

    public List<Product> getWishlist() {
        return wishlist;
    }

    // ---- Order history ----

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }
}

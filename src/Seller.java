import java.util.ArrayList;
import java.util.List;

/**
 * A merchant account. Unlike the single hard-coded seller in the original
 * project, ShopSphere supports many sellers, each running their own store
 * and product catalog inside one shared marketplace.
 */
public class Seller extends User {
    private String storeName;
    private List<Product> products = new ArrayList<>();

    public Seller(String userId, String name, String email, String storeName) {
        super(userId, name, email);
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public boolean removeProduct(String productId) {
        return products.removeIf(p -> p.getProductId().equals(productId));
    }

    public List<Product> getProducts() {
        return products;
    }
}

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The shared marketplace all sellers and buyers plug into. This is the
 * main structural difference from the original single-seller project:
 * there is no one global Seller object, just a catalog assembled from
 * however many sellers have registered.
 */
public class Marketplace {
    private List<Seller> sellers = new ArrayList<>();
    private List<Buyer> buyers = new ArrayList<>();
    private List<Order> allOrders = new ArrayList<>();

    public void registerSeller(Seller seller) {
        sellers.add(seller);
    }

    public List<Seller> getSellers() {
        return sellers;
    }

    public void registerBuyer(Buyer buyer) {
        buyers.add(buyer);
    }

    public List<Buyer> getBuyers() {
        return buyers;
    }

    /** Removes a seller and, with it, every product they had listed. */
    public boolean removeSeller(String sellerId) {
        return sellers.removeIf(s -> s.getUserId().equals(sellerId));
    }

    /** Admin moderation: delist a product no matter which seller owns it. */
    public boolean removeProductAnywhere(String productId) {
        for (Seller seller : sellers) {
            if (seller.removeProduct(productId)) {
                return true;
            }
        }
        return false;
    }

    /** Records a completed order in the platform-wide order log (admin visibility). */
    public void recordOrder(Order order) {
        allOrders.add(order);
    }

    public List<Order> getAllOrders() {
        return allOrders;
    }

    public List<Product> getAllProducts() {
        List<Product> all = new ArrayList<>();
        for (Seller seller : sellers) {
            all.addAll(seller.getProducts());
        }
        return all;
    }

    /**
     * Returns products matching a keyword (name or description) and an
     * optional category, sorted according to sortLabel.
     *
     * @param keyword  free-text search term; null or blank matches everything
     * @param category filter; null means "all categories"
     * @param sortLabel one of "Price: Low to High", "Price: High to Low",
     *                  "Name: A-Z", or anything else for no particular order
     */
    public List<Product> searchAndFilter(String keyword, Category category, String sortLabel) {
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();

        List<Product> results = new ArrayList<>();
        for (Product product : getAllProducts()) {
            boolean matchesKeyword = kw.isEmpty()
                    || product.getName().toLowerCase().contains(kw)
                    || product.getDescription().toLowerCase().contains(kw);
            boolean matchesCategory = (category == null) || product.getCategory() == category;
            if (matchesKeyword && matchesCategory) {
                results.add(product);
            }
        }

        if (sortLabel != null) {
            switch (sortLabel) {
                case "Price: Low to High":
                    results.sort(Comparator.comparingDouble(Product::getPrice));
                    break;
                case "Price: High to Low":
                    results.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                    break;
                case "Name: A-Z":
                    results.sort(Comparator.comparing(Product::getName));
                    break;
                default:
                    break; // "Default" -> leave in catalog order
            }
        }
        return results;
    }
}

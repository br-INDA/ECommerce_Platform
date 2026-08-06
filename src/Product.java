/**
 * A single listing in the marketplace. Every product belongs to exactly
 * one seller (identified here by store name) and one category.
 */
public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Category category;
    private String sellerName;
    private double rating;
    private boolean featured;

    /** Standard listing: no rating yet (shown as "New Arrival"), not featured. */
    public Product(String productId, String name, String description, double price,
                    int stock, Category category, String sellerName) {
        this(productId, name, description, price, stock, category, sellerName, 0.0, false);
    }

    /** Full constructor, mainly used for demo-seeded listings that already have reviews. */
    public Product(String productId, String name, String description, double price,
                    int stock, Category category, String sellerName, double rating, boolean featured) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.sellerName = sellerName;
        this.rating = rating;
        this.featured = featured;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Category getCategory() {
        return category;
    }

    public String getSellerName() {
        return sellerName;
    }

    public double getRating() {
        return rating;
    }

    public boolean isFeatured() {
        return featured;
    }

    /**
     * Attempts to deduct quantity from stock (used at checkout).
     * Returns false if there isn't enough stock left.
     */
    public boolean reduceStock(int quantity) {
        if (quantity > stock) {
            return false;
        }
        stock -= quantity;
        return true;
    }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}

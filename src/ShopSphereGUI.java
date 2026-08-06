import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ShopSphere — a multi-seller marketplace, built on the same core ideas as
 * the original single-seller ECommerceGUI project (roles, product forms,
 * cart, checkout) but reworked as a bigger, standalone practice project:
 *
 *   - Multiple independent sellers instead of one hard-coded store
 *   - Cart quantities (not just "one of each"), with a live subtotal
 *   - Search, category filter and sorting across the whole catalog
 *   - A wishlist, separate from the cart
 *   - Order history per buyer, with a coupon code applied at checkout
 *   - An Admin dashboard: platform-wide stats, seller/product moderation,
 *     and a full order log across every buyer
 *   - A branded indigo/amber color system (see styles.css) instead of
 *     default JavaFX gray, with color-coded category badges
 *   - Every screen is rebuilt fresh on navigation, so it always reflects
 *     the latest data
 *
 * This is a learning/practice project only — it is not tied to any
 * assignment or submission.
 */
public class ShopSphereGUI extends Application {

    private static final double WIDTH = 980;
    private static final double HEIGHT = 700;

    private Stage primaryStage;
    private Marketplace marketplace;
    private Seller currentSeller;
    private Buyer currentBuyer;
    private double appliedDiscount = 0.0;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        marketplace = new Marketplace();
        seedDemoData();

        primaryStage.setTitle("ShopSphere — Multi-Seller Marketplace");
        primaryStage.setScene(createStyledScene(createWelcomePane()));
        primaryStage.show();
    }

    /** Pre-populates a few sellers and products so the marketplace isn't empty on first run. */
    private void seedDemoData() {
        Seller techStore = new Seller("SEL001", "Tech Store Admin", "tech@shopsphere.com", "TechStore");
        techStore.addProduct(new Product("P001", "Wireless Mouse", "Ergonomic 2.4GHz wireless mouse", 19.99, 50, Category.ELECTRONICS, "TechStore", 4.3, false));
        techStore.addProduct(new Product("P002", "Mechanical Keyboard", "RGB backlit hot-swappable keyboard", 59.99, 30, Category.ELECTRONICS, "TechStore", 4.7, true));
        marketplace.registerSeller(techStore);

        Seller bookNook = new Seller("SEL002", "Book Nook Admin", "books@shopsphere.com", "BookNook");
        bookNook.addProduct(new Product("P003", "Clean Code", "A handbook of agile software craftsmanship", 34.99, 20, Category.BOOKS, "BookNook", 4.8, true));
        bookNook.addProduct(new Product("P004", "Atomic Habits", "Practical guide to building good habits", 15.99, 40, Category.BOOKS, "BookNook", 4.6, false));
        marketplace.registerSeller(bookNook);

        Seller urbanWear = new Seller("SEL003", "Urban Wear Admin", "wear@shopsphere.com", "UrbanWear");
        urbanWear.addProduct(new Product("P005", "Cotton T-Shirt", "Everyday crew neck tee, unisex fit", 12.99, 100, Category.CLOTHING, "UrbanWear", 4.1, false));
        urbanWear.addProduct(new Product("P006", "Running Shoes", "Lightweight breathable running shoes", 44.99, 25, Category.SPORTS, "UrbanWear", 4.4, false));
        marketplace.registerSeller(urbanWear);
    }

    // =========================================================
    // STYLING HELPERS
    // =========================================================

    /** Wraps a pane in a Scene and attaches styles.css (falls back to default styling if the file is missing). */
    private Scene createStyledScene(Pane root) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        try {
            File css = new File("styles.css");
            if (css.exists()) {
                scene.getStylesheets().add(css.toURI().toURL().toExternalForm());
            }
        } catch (Exception ex) {
            // styles.css not found next to the run directory - continue with default styling
        }
        return scene;
    }

    /** The colored bar shown at the top of every screen. */
    private HBox createHeaderBar(String title, String subtitle) {
        VBox textBox = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("app-header-title");
        textBox.getChildren().add(titleLabel);
        if (subtitle != null && !subtitle.isBlank()) {
            Label subLabel = new Label(subtitle);
            subLabel.getStyleClass().add("app-header-subtitle");
            textBox.getChildren().add(subLabel);
        }
        HBox header = new HBox(textBox);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox createHeaderBar(String title) {
        return createHeaderBar(title, null);
    }

    private Button styledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    // =========================================================
    // WELCOME / ROLE SELECTION
    // =========================================================

    private Pane createWelcomePane() {
        VBox root = new VBox(20);
        root.getStyleClass().add("welcome-bg");
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Welcome to ShopSphere");
        title.getStyleClass().add("welcome-title");

        Label tagline = new Label("Shop smarter across every store, all in one place \u2728");
        tagline.getStyleClass().add("welcome-tagline");

        TabPane roleTabs = new TabPane();
        roleTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        roleTabs.setMaxWidth(520);

        Tab sellerTab = new Tab("I'm a Seller", createSellerLoginForm());
        Tab buyerTab = new Tab("I'm a Buyer", createBuyerLoginForm());
        Tab adminTab = new Tab("Admin", createAdminLoginForm());
        roleTabs.getTabs().addAll(sellerTab, buyerTab, adminTab);

        root.getChildren().addAll(title, tagline, roleTabs);
        return root;
    }

    private Pane createSellerLoginForm() {
        GridPane form = new GridPane();
        form.getStyleClass().add("form-card");
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        idField.setPromptText("Seller ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField storeNameField = new TextField();
        storeNameField.setPromptText("Store Name");
        Button continueBtn = styledButton("Continue as Seller", "btn-primary");

        form.addRow(0, new Label("Seller ID:"), idField);
        form.addRow(1, new Label("Name:"), nameField);
        form.addRow(2, new Label("Email:"), emailField);
        form.addRow(3, new Label("Store Name:"), storeNameField);
        form.add(continueBtn, 0, 4, 2, 1);
        GridPane.setHalignment(continueBtn, javafx.geometry.HPos.CENTER);

        continueBtn.setOnAction(e -> {
            if (idField.getText().isBlank() || storeNameField.getText().isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Seller ID and Store Name are required.");
                return;
            }
            currentSeller = findOrCreateSeller(idField.getText().trim(), nameField.getText(),
                    emailField.getText(), storeNameField.getText().trim());
            primaryStage.setScene(createStyledScene(createSellerDashboardPane()));
        });

        return wrapCentered(form);
    }

    private Pane createBuyerLoginForm() {
        GridPane form = new GridPane();
        form.getStyleClass().add("form-card");
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        idField.setPromptText("Buyer ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField addressField = new TextField();
        addressField.setPromptText("Delivery Address");
        Button continueBtn = styledButton("Continue as Buyer", "btn-primary");

        form.addRow(0, new Label("Buyer ID:"), idField);
        form.addRow(1, new Label("Name:"), nameField);
        form.addRow(2, new Label("Email:"), emailField);
        form.addRow(3, new Label("Address:"), addressField);
        form.add(continueBtn, 0, 4, 2, 1);
        GridPane.setHalignment(continueBtn, javafx.geometry.HPos.CENTER);

        continueBtn.setOnAction(e -> {
            if (idField.getText().isBlank() || nameField.getText().isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Buyer ID and Name are required.");
                return;
            }
            currentBuyer = findOrCreateBuyer(idField.getText().trim(), nameField.getText(),
                    emailField.getText(), addressField.getText());
            appliedDiscount = 0.0;
            primaryStage.setScene(createStyledScene(createMarketplacePane()));
        });

        return wrapCentered(form);
    }

    private Pane createAdminLoginForm() {
        VBox box = new VBox(16);
        box.getStyleClass().add("form-card");
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(400);

        Label info = new Label("View platform-wide stats, manage sellers,\nmoderate listings, and review every order.");
        info.getStyleClass().add("muted-text");
        info.setWrapText(true);
        info.setStyle("-fx-text-alignment: center;");

        Button enterBtn = styledButton("Enter Admin Dashboard", "btn-primary");
        enterBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createAdminDashboardPane())));

        box.getChildren().addAll(info, enterBtn);
        return wrapCentered(box);
    }

    private Pane wrapCentered(Pane content) {
        VBox wrapper = new VBox(content);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(20, 0, 0, 0));
        return wrapper;
    }

    private Seller findOrCreateSeller(String id, String name, String email, String storeName) {
        for (Seller s : marketplace.getSellers()) {
            if (s.getUserId().equals(id)) {
                return s;
            }
        }
        Seller newSeller = new Seller(id, name, email, storeName);
        marketplace.registerSeller(newSeller);
        return newSeller;
    }

    private Buyer findOrCreateBuyer(String id, String name, String email, String address) {
        for (Buyer b : marketplace.getBuyers()) {
            if (b.getUserId().equals(id)) {
                return b;
            }
        }
        Buyer newBuyer = new Buyer(id, name, email, address);
        marketplace.registerBuyer(newBuyer);
        return newBuyer;
    }

    // =========================================================
    // SELLER DASHBOARD
    // =========================================================

    private Pane createSellerDashboardPane() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("seller-bg");

        HBox header = createHeaderBar("Seller Dashboard", currentSeller.getStoreName());

        int totalProducts = currentSeller.getProducts().size();
        int totalStock = 0;
        double catalogValue = 0;
        for (Product p : currentSeller.getProducts()) {
            totalStock += p.getStock();
            catalogValue += p.getPrice() * p.getStock();
        }
        HBox statsRow = new HBox(16,
                buildStatCard("Products Listed", String.valueOf(totalProducts), "stat-card-indigo"),
                buildStatCard("Total Stock Units", String.valueOf(totalStock), "stat-card-amber"),
                buildStatCard("Catalog Value", String.format("$%.2f", catalogValue), "stat-card-teal"));
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(15, 20, 5, 20));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(15, 20, 10, 20));

        TextField idField = new TextField();
        idField.setPromptText("Product ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");
        ComboBox<Category> categoryBox = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        categoryBox.setPromptText("Category");

        form.addRow(0, new Label("Product ID:"), idField, new Label("Category:"), categoryBox);
        form.addRow(1, new Label("Name:"), nameField, new Label("Price ($):"), priceField);
        form.addRow(2, new Label("Description:"), descField, new Label("Stock:"), stockField);

        Button addBtn = styledButton("Add Product", "btn-primary");
        Button removeBtn = styledButton("Remove Selected", "btn-danger");
        Button backBtn = styledButton("Back to Home", "btn-secondary");
        HBox btnBox = new HBox(10, addBtn, removeBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(5, 20, 10, 20));

        TableView<Product> table = buildProductTable(currentSeller.getProducts());

        addBtn.setOnAction(e -> {
            if (categoryBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Please choose a category.");
                return;
            }
            try {
                Product product = new Product(
                        idField.getText(),
                        nameField.getText(),
                        descField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockField.getText()),
                        categoryBox.getValue(),
                        currentSeller.getStoreName()
                );
                currentSeller.addProduct(product);
                showAlert(Alert.AlertType.INFORMATION, "Product added successfully!");
                primaryStage.setScene(createStyledScene(createSellerDashboardPane()));
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Please enter valid numbers for price and stock.");
            }
        });

        removeBtn.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "Select a product in the table first.");
                return;
            }
            currentSeller.removeProduct(selected.getProductId());
            primaryStage.setScene(createStyledScene(createSellerDashboardPane()));
        });

        backBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createWelcomePane())));

        VBox top = new VBox(header, statsRow, form, btnBox);
        pane.setTop(top);
        BorderPane.setMargin(table, new Insets(0, 20, 20, 20));
        pane.setCenter(table);
        return pane;
    }

    private TableView<Product> buildProductTable(List<Product> products) {
        TableView<Product> table = new TableView<>(FXCollections.observableArrayList(products));

        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductId()));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getPrice())));

        TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        stockCol.setCellFactory(col -> stockBadgeCell());

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().toString()));
        catCol.setCellFactory(col -> categoryBadgeCell());

        table.getColumns().addAll(idCol, nameCol, priceCol, stockCol, catCol);
        return table;
    }

    // =========================================================
    // MARKETPLACE (BUYER BROWSE / SEARCH / FILTER)
    // =========================================================

    private Pane createMarketplacePane() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("buyer-bg");

        HBox header = createHeaderBar("Marketplace", "Welcome, " + currentBuyer.getName());

        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All Categories");
        for (Category c : Category.values()) {
            categoryFilter.getItems().add(c.toString());
        }
        categoryFilter.setValue("All Categories");

        ComboBox<String> sortBox = new ComboBox<>(FXCollections.observableArrayList(
                "Default", "Price: Low to High", "Price: High to Low", "Name: A-Z"));
        sortBox.setValue("Default");

        Button searchBtn = styledButton("Search", "btn-primary");

        HBox controls = new HBox(10, searchField, categoryFilter, sortBox, searchBtn);
        controls.getStyleClass().add("filter-bar");
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(12, 20, 12, 20));
        HBox.setMargin(controls, new Insets(0, 20, 0, 20));

        Label promoText = new Label("\uD83C\uDF89 New here? Use WELCOME5 for 5% off \u2014 or try SAVE10 / SAVE20 at checkout!");
        promoText.getStyleClass().add("promo-text");
        HBox promoBanner = new HBox(promoText);
        promoBanner.getStyleClass().add("promo-banner");
        promoBanner.setAlignment(Pos.CENTER);

        FlowPane productGrid = new FlowPane(15, 15);
        productGrid.setPadding(new Insets(15, 20, 15, 20));
        ScrollPane scrollPane = new ScrollPane(productGrid);
        scrollPane.setFitToWidth(true);

        Runnable refreshGrid = () -> {
            productGrid.getChildren().clear();
            Category selectedCategory = categoryFilter.getValue().equals("All Categories")
                    ? null : Category.valueOf(categoryFilter.getValue());
            List<Product> results = marketplace.searchAndFilter(
                    searchField.getText(), selectedCategory, sortBox.getValue());
            if (results.isEmpty()) {
                productGrid.getChildren().add(new Label("No products found."));
            } else {
                for (Product p : results) {
                    productGrid.getChildren().add(buildProductCard(p));
                }
            }
        };
        refreshGrid.run();

        searchBtn.setOnAction(e -> refreshGrid.run());
        categoryFilter.setOnAction(e -> refreshGrid.run());
        sortBox.setOnAction(e -> refreshGrid.run());

        FlowPane chipRow = new FlowPane(8, 8);
        chipRow.setPadding(new Insets(12, 20, 0, 20));
        Button allChip = styledButton("All", "chip-all");
        allChip.setOnAction(e -> {
            categoryFilter.setValue("All Categories");
            refreshGrid.run();
        });
        chipRow.getChildren().add(allChip);
        for (Category c : Category.values()) {
            Button chip = styledButton(c.toString(), "chip-" + c.toString().toLowerCase());
            chip.setOnAction(e -> {
                categoryFilter.setValue(c.toString());
                refreshGrid.run();
            });
            chipRow.getChildren().add(chip);
        }

        Button cartBtn = styledButton("Cart (" + totalCartItems() + ")", "btn-primary");
        Button wishlistBtn = styledButton("Wishlist (" + currentBuyer.getWishlist().size() + ")", "btn-amber");
        Button ordersBtn = styledButton("Order History", "btn-teal");
        Button backBtn = styledButton("Back to Home", "btn-secondary");
        HBox bottomBtns = new HBox(10, cartBtn, wishlistBtn, ordersBtn, backBtn);
        bottomBtns.setAlignment(Pos.CENTER);
        bottomBtns.setPadding(new Insets(10, 0, 15, 0));

        cartBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createCartPane())));
        wishlistBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createWishlistPane())));
        ordersBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createOrderHistoryPane())));
        backBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createWelcomePane())));

        VBox top = new VBox(header, promoBanner, chipRow, controls);
        pane.setTop(top);
        pane.setCenter(scrollPane);
        pane.setBottom(bottomBtns);
        return pane;
    }

    /** Big emoji shown at the top of each product card so the grid isn't all text. */
    private String categoryEmoji(Category category) {
        switch (category) {
            case ELECTRONICS:   return "\uD83D\uDCF1";
            case BOOKS:         return "\uD83D\uDCDA";
            case CLOTHING:      return "\uD83D\uDC55";
            case HOME_KITCHEN:  return "\uD83C\uDFE0";
            case GROCERY:       return "\uD83C\uDF4E";
            case SPORTS:        return "\u26BD";
            default:            return "\uD83D\uDCE6";
        }
    }

    /** Renders a 5-star row (rounded to the nearest whole star) plus the numeric score. */
    private String starString(double rating) {
        int filled = (int) Math.round(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < filled ? "\u2605" : "\u2606");
        }
        return sb + String.format("  (%.1f)", rating);
    }

    private Pane buildProductCard(Product p) {
        VBox card = new VBox(6);
        card.getStyleClass().add("product-card");
        card.setPadding(new Insets(12));
        card.setPrefWidth(220);

        Label emojiLabel = new Label(categoryEmoji(p.getCategory()));
        emojiLabel.getStyleClass().add("card-emoji");

        HBox badgeRow = new HBox(6);
        Label catBadge = new Label(p.getCategory().toString());
        catBadge.getStyleClass().addAll("badge", "badge-" + p.getCategory().toString().toLowerCase());
        badgeRow.getChildren().add(catBadge);
        if (p.isFeatured()) {
            Label popularBadge = new Label("\uD83D\uDD25 Popular");
            popularBadge.getStyleClass().add("badge-featured");
            badgeRow.getChildren().add(popularBadge);
        }

        Label nameLabel = new Label(p.getName());
        nameLabel.getStyleClass().add("card-title");
        Label storeLabel = new Label("Sold by: " + p.getSellerName());
        storeLabel.getStyleClass().add("store-label");

        Label ratingLabel = new Label(p.getRating() > 0 ? starString(p.getRating()) : "New Arrival");
        ratingLabel.getStyleClass().add(p.getRating() > 0 ? "rating-stars" : "new-badge");

        Label priceLabel = new Label(String.format("$%.2f", p.getPrice()));
        priceLabel.getStyleClass().add("price-tag");
        Label stockLabel = new Label(p.getStock() > 0 ? "In stock: " + p.getStock() : "Out of stock");
        stockLabel.getStyleClass().add(p.getStock() > 0 ? "stock-in" : "stock-out");

        Spinner<Integer> qtySpinner = new Spinner<>(1, Math.max(p.getStock(), 1), 1);
        qtySpinner.setPrefWidth(70);
        qtySpinner.setDisable(p.getStock() == 0);

        Button addToCartBtn = styledButton("Add to Cart", "btn-primary");
        addToCartBtn.setDisable(p.getStock() == 0);
        Button wishlistBtn = styledButton("\u2665 Wishlist", "btn-amber");

        addToCartBtn.setOnAction(e -> {
            currentBuyer.addToCart(p, qtySpinner.getValue());
            showAlert(Alert.AlertType.INFORMATION, qtySpinner.getValue() + " x " + p.getName() + " added to cart!");
            primaryStage.setScene(createStyledScene(createMarketplacePane()));
        });

        wishlistBtn.setOnAction(e -> {
            currentBuyer.addToWishlist(p);
            showAlert(Alert.AlertType.INFORMATION, p.getName() + " added to wishlist!");
        });

        HBox btnBox = new HBox(6, addToCartBtn, wishlistBtn);
        card.getChildren().addAll(emojiLabel, badgeRow, nameLabel, storeLabel, ratingLabel,
                priceLabel, stockLabel, qtySpinner, btnBox);
        return card;
    }

    private int totalCartItems() {
        int total = 0;
        for (int qty : currentBuyer.getCart().values()) {
            total += qty;
        }
        return total;
    }

    // =========================================================
    // CART
    // =========================================================

    private Pane createCartPane() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("buyer-bg");

        HBox header = createHeaderBar("Your Cart");

        VBox itemsBox = new VBox(10);
        itemsBox.setPadding(new Insets(15, 20, 15, 20));
        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);

        for (Map.Entry<Product, Integer> entry : currentBuyer.getCart().entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();

            HBox row = new HBox(15);
            row.getStyleClass().add("cart-row");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));

            Label nameLabel = new Label(p.getName());
            nameLabel.setPrefWidth(200);
            Label priceLabel = new Label(String.format("$%.2f each", p.getPrice()));

            Spinner<Integer> qtySpinner = new Spinner<>(1, Math.max(p.getStock(), qty), qty);
            qtySpinner.setPrefWidth(70);
            qtySpinner.valueProperty().addListener((obs, oldV, newV) ->
                    currentBuyer.updateCartQuantity(p, newV));

            Label subtotalLabel = new Label(String.format("$%.2f", p.getPrice() * qty));
            subtotalLabel.getStyleClass().add("price-tag");

            Button removeBtn = styledButton("Remove", "btn-danger");
            removeBtn.setOnAction(e -> {
                currentBuyer.removeFromCart(p);
                primaryStage.setScene(createStyledScene(createCartPane()));
            });

            row.getChildren().addAll(nameLabel, priceLabel, new Label("Qty:"), qtySpinner, subtotalLabel, removeBtn);
            itemsBox.getChildren().add(row);
        }

        if (currentBuyer.getCart().isEmpty()) {
            itemsBox.getChildren().add(new Label("Your cart is empty."));
        }

        TextField couponField = new TextField();
        couponField.setPromptText("Coupon code (try SAVE10)");
        Button applyCouponBtn = styledButton("Apply", "btn-secondary");
        Label discountLabel = new Label();
        Label totalLabel = new Label();
        totalLabel.getStyleClass().add("page-title");

        Runnable updateTotals = () -> {
            double subtotal = currentBuyer.getCartTotal();
            double discount = subtotal * appliedDiscount;
            discountLabel.setText(appliedDiscount > 0
                    ? String.format("Discount: -$%.2f (%.0f%% off)", discount, appliedDiscount * 100)
                    : "");
            totalLabel.setText(String.format("Total: $%.2f", subtotal - discount));
        };
        updateTotals.run();

        applyCouponBtn.setOnAction(e -> {
            double discount = Coupon.getDiscountPercent(couponField.getText());
            if (discount == 0.0) {
                showAlert(Alert.AlertType.ERROR, "Invalid coupon code.");
            } else {
                appliedDiscount = discount;
                showAlert(Alert.AlertType.INFORMATION, "Coupon applied!");
            }
            updateTotals.run();
        });

        Button checkoutBtn = styledButton("Checkout", "btn-primary");
        checkoutBtn.setDisable(currentBuyer.getCart().isEmpty());
        Button backBtn = styledButton("Continue Shopping", "btn-secondary");

        checkoutBtn.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("PayPal", "PayPal", "Credit Card", "UPI");
            dialog.setTitle("Checkout");
            dialog.setHeaderText("Select Payment Method");
            dialog.showAndWait().ifPresent(choice -> {
                PaymentMethod paymentMethod;
                switch (choice) {
                    case "Credit Card":
                        paymentMethod = new CreditCard();
                        break;
                    case "UPI":
                        paymentMethod = new UPI();
                        break;
                    default:
                        paymentMethod = new PayPal();
                }

                double subtotal = currentBuyer.getCartTotal();
                double finalTotal = subtotal - (subtotal * appliedDiscount);
                paymentMethod.setAmount(finalTotal);
                boolean success = paymentMethod.processPayment();

                if (success) {
                    List<CartItem> orderItems = new ArrayList<>();
                    for (Map.Entry<Product, Integer> entry : currentBuyer.getCart().entrySet()) {
                        entry.getKey().reduceStock(entry.getValue());
                        orderItems.add(new CartItem(entry.getKey(), entry.getValue()));
                    }
                    Order order = new Order("ORD" + System.currentTimeMillis(), currentBuyer,
                            orderItems, finalTotal, paymentMethod.getMethodName());
                    currentBuyer.addOrder(order);
                    marketplace.recordOrder(order);
                    currentBuyer.clearCart();
                    appliedDiscount = 0.0;

                    showAlert(Alert.AlertType.INFORMATION,
                            String.format("Order placed successfully!%nTotal charged: $%.2f via %s",
                                    finalTotal, paymentMethod.getMethodName()));
                    primaryStage.setScene(createStyledScene(createMarketplacePane()));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Payment failed. Please try again.");
                }
            });
        });

        backBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createMarketplacePane())));

        HBox couponBox = new HBox(10, couponField, applyCouponBtn);
        couponBox.setAlignment(Pos.CENTER);

        Label trustBadges = new Label("\uD83D\uDD12 Secure Checkout   \u2022   \uD83D\uDE9A Fast Delivery   \u2022   \u21A9 Easy Returns");
        trustBadges.getStyleClass().add("trust-badges");

        HBox checkoutBox = new HBox(10, checkoutBtn, backBtn);
        checkoutBox.setAlignment(Pos.CENTER);
        VBox bottomBox = new VBox(10, couponBox, discountLabel, totalLabel, trustBadges, checkoutBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10, 0, 20, 0));

        pane.setTop(header);
        pane.setCenter(scrollPane);
        pane.setBottom(bottomBox);
        return pane;
    }

    // =========================================================
    // WISHLIST
    // =========================================================

    private Pane createWishlistPane() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("buyer-bg");

        HBox header = createHeaderBar("Your Wishlist");

        VBox box = new VBox(10);
        box.setPadding(new Insets(15, 20, 15, 20));
        for (Product p : currentBuyer.getWishlist()) {
            HBox row = new HBox(15);
            row.getStyleClass().add("cart-row");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));

            Label nameLabel = new Label(p.getName() + " - " + String.format("$%.2f", p.getPrice()));
            nameLabel.setPrefWidth(300);
            Button moveToCartBtn = styledButton("Move to Cart", "btn-primary");
            Button removeBtn = styledButton("Remove", "btn-danger");

            moveToCartBtn.setOnAction(e -> {
                currentBuyer.addToCart(p, 1);
                currentBuyer.removeFromWishlist(p);
                primaryStage.setScene(createStyledScene(createWishlistPane()));
            });
            removeBtn.setOnAction(e -> {
                currentBuyer.removeFromWishlist(p);
                primaryStage.setScene(createStyledScene(createWishlistPane()));
            });

            row.getChildren().addAll(nameLabel, moveToCartBtn, removeBtn);
            box.getChildren().add(row);
        }
        if (currentBuyer.getWishlist().isEmpty()) {
            box.getChildren().add(new Label("Your wishlist is empty."));
        }

        Button backBtn = styledButton("Back to Marketplace", "btn-secondary");
        backBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createMarketplacePane())));
        HBox bottomBox = new HBox(backBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(0, 0, 20, 0));

        pane.setTop(header);
        pane.setCenter(new ScrollPane(box));
        pane.setBottom(bottomBox);
        return pane;
    }

    // =========================================================
    // ORDER HISTORY (per buyer)
    // =========================================================

    private Pane createOrderHistoryPane() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("buyer-bg");

        HBox header = createHeaderBar("Order History");

        TableView<Order> table = new TableView<>(FXCollections.observableArrayList(currentBuyer.getOrderHistory()));

        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderId()));

        TableColumn<Order, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderDate().toLocalDate().toString()));

        TableColumn<Order, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getItems().size())));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getTotalAmount())));

        TableColumn<Order, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentMethodName()));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));
        statusCol.setCellFactory(col -> statusBadgeCell());

        table.getColumns().addAll(idCol, dateCol, itemsCol, totalCol, paymentCol, statusCol);

        Button backBtn = styledButton("Back to Marketplace", "btn-secondary");
        backBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createMarketplacePane())));
        HBox bottomBox = new HBox(backBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15, 0, 20, 0));

        pane.setTop(header);
        BorderPane.setMargin(table, new Insets(20));
        pane.setCenter(table);
        pane.setBottom(bottomBox);
        return pane;
    }

    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    private Pane createAdminDashboardPane() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("admin-bg");

        HBox header = createHeaderBar("Admin Dashboard", "Platform-wide view across every seller and buyer");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Overview", createAdminOverviewPane()),
                new Tab("Sellers", createAdminSellersPane()),
                new Tab("Products", createAdminProductsPane()),
                new Tab("Orders", createAdminOrdersPane())
        );

        VBox content = new VBox(tabs);
        content.setPadding(new Insets(20));
        VBox.setVgrow(tabs, Priority.ALWAYS);

        Button backBtn = styledButton("Back to Home", "btn-secondary");
        backBtn.setOnAction(e -> primaryStage.setScene(createStyledScene(createWelcomePane())));
        HBox bottomBox = new HBox(backBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(0, 0, 20, 0));

        pane.setTop(header);
        pane.setCenter(content);
        pane.setBottom(bottomBox);
        return pane;
    }

    private Pane createAdminOverviewPane() {
        VBox box = new VBox(24);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.TOP_CENTER);

        int totalSellers = marketplace.getSellers().size();
        int totalBuyers = marketplace.getBuyers().size();
        int totalProducts = marketplace.getAllProducts().size();
        int totalOrders = marketplace.getAllOrders().size();
        double totalRevenue = 0.0;
        for (Order o : marketplace.getAllOrders()) {
            if (o.getStatus() == OrderStatus.CONFIRMED) {
                totalRevenue += o.getTotalAmount();
            }
        }

        HBox statsRow = new HBox(16,
                buildStatCard("Sellers", String.valueOf(totalSellers), "stat-card-indigo"),
                buildStatCard("Buyers", String.valueOf(totalBuyers), "stat-card-amber"),
                buildStatCard("Products Listed", String.valueOf(totalProducts), "stat-card-teal"),
                buildStatCard("Orders Placed", String.valueOf(totalOrders), "stat-card-coral"));
        statsRow.setAlignment(Pos.CENTER);

        Label revenueLabel = new Label(String.format("\uD83D\uDCB0 Total Revenue (confirmed orders): $%.2f", totalRevenue));
        revenueLabel.getStyleClass().add("revenue-text");
        HBox revenueBanner = new HBox(revenueLabel);
        revenueBanner.getStyleClass().add("revenue-banner");
        revenueBanner.setAlignment(Pos.CENTER);
        revenueBanner.setMaxWidth(520);

        box.getChildren().addAll(statsRow, revenueBanner);
        return box;
    }

    private Pane buildStatCard(String label, String value, String colorClass) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("stat-card", colorClass);
        card.setAlignment(Pos.CENTER);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("stat-label");
        card.getChildren().addAll(valueLabel, nameLabel);
        return card;
    }

    private Pane createAdminSellersPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(15, 0, 0, 0));

        TableView<Seller> table = new TableView<>(FXCollections.observableArrayList(marketplace.getSellers()));

        TableColumn<Seller, String> avatarCol = new TableColumn<>("");
        avatarCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStoreName()));
        avatarCol.setCellFactory(col -> avatarCell());
        avatarCol.setSortable(false);
        avatarCol.setPrefWidth(50);
        avatarCol.setResizable(false);

        TableColumn<Seller, String> idCol = new TableColumn<>("Seller ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserId()));
        TableColumn<Seller, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<Seller, String> storeCol = new TableColumn<>("Store");
        storeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStoreName()));
        TableColumn<Seller, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        TableColumn<Seller, String> countCol = new TableColumn<>("Products");
        countCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getProducts().size())));

        table.getColumns().addAll(avatarCol, idCol, nameCol, storeCol, emailCol, countCol);

        Button removeBtn = styledButton("Remove Seller", "btn-danger");
        removeBtn.setOnAction(e -> {
            Seller selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "Select a seller first.");
                return;
            }
            marketplace.removeSeller(selected.getUserId());
            primaryStage.setScene(createStyledScene(createAdminDashboardPane()));
        });

        HBox btnBox = new HBox(removeBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        pane.setCenter(table);
        pane.setBottom(btnBox);
        return pane;
    }

    private Pane createAdminProductsPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(15, 0, 0, 0));

        TableView<Product> table = new TableView<>(FXCollections.observableArrayList(marketplace.getAllProducts()));

        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductId()));
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<Product, String> storeCol = new TableColumn<>("Store");
        storeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSellerName()));
        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().toString()));
        catCol.setCellFactory(col -> categoryBadgeCell());
        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getPrice())));
        TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        stockCol.setCellFactory(col -> stockBadgeCell());

        table.getColumns().addAll(idCol, nameCol, storeCol, catCol, priceCol, stockCol);

        Button removeBtn = styledButton("Remove Product", "btn-danger");
        removeBtn.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "Select a product first.");
                return;
            }
            marketplace.removeProductAnywhere(selected.getProductId());
            primaryStage.setScene(createStyledScene(createAdminDashboardPane()));
        });

        HBox btnBox = new HBox(removeBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        pane.setCenter(table);
        pane.setBottom(btnBox);
        return pane;
    }

    private Pane createAdminOrdersPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(15, 0, 0, 0));

        TableView<Order> table = new TableView<>(FXCollections.observableArrayList(marketplace.getAllOrders()));

        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderId()));
        TableColumn<Order, String> buyerCol = new TableColumn<>("Buyer");
        buyerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBuyer().getName()));
        TableColumn<Order, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderDate().toLocalDate().toString()));
        TableColumn<Order, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getItems().size())));
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getTotalAmount())));
        TableColumn<Order, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentMethodName()));
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));
        statusCol.setCellFactory(col -> statusBadgeCell());

        table.getColumns().addAll(idCol, buyerCol, dateCol, itemsCol, totalCol, paymentCol, statusCol);

        Button cancelBtn = styledButton("Cancel Selected Order", "btn-danger");
        cancelBtn.setOnAction(e -> {
            Order selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "Select an order first.");
                return;
            }
            selected.setStatus(OrderStatus.CANCELLED);
            primaryStage.setScene(createStyledScene(createAdminDashboardPane()));
        });

        HBox btnBox = new HBox(cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        pane.setCenter(table);
        pane.setBottom(btnBox);
        return pane;
    }

    // =========================================================
    // COLORED TABLE CELL HELPERS
    // =========================================================

    /** Renders a category string as a colored pill, matching the product card badges. */
    private TableCell<Product, String> categoryBadgeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().addAll("badge", "badge-" + item.toLowerCase());
                setGraphic(badge);
                setText(null);
            }
        };
    }

    /** Renders a stock count as a green/amber/red pill depending on how low it is. */
    private TableCell<Product, String> stockBadgeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                int stock = Integer.parseInt(item);
                Label badge = new Label(stock == 0 ? "Out of stock" : String.valueOf(stock));
                badge.getStyleClass().add(stock == 0 ? "stock-badge-out" : stock <= 10 ? "stock-badge-low" : "stock-badge-good");
                setGraphic(badge);
                setText(null);
            }
        };
    }

    /** Renders an order status string as a green (confirmed) or red (cancelled) pill. */
    private TableCell<Order, String> statusBadgeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add(item.equals("CONFIRMED") ? "status-confirmed" : "status-cancelled");
                setGraphic(badge);
                setText(null);
            }
        };
    }

    /** Renders a store name as a small colored circular initial, for a bit of visual variety in the sellers table. */
    private TableCell<Seller, String> avatarCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                    return;
                }
                Label circle = new Label(item.substring(0, 1).toUpperCase());
                int colorIdx = Math.floorMod(item.hashCode(), 5);
                circle.getStyleClass().addAll("avatar-chip", "avatar-" + colorIdx, "avatar-label");
                setGraphic(circle);
                setText(null);
            }
        };
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

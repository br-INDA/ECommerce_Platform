# ShopSphere — Multi-Seller Marketplace (JavaFX)

A standalone practice project, not tied to any assignment or submission.
It shares the same core idea as your original `ECommerceGUI.java` — Seller
and Buyer roles, product forms, a cart, and checkout — but is built out as
a bigger, independent project rather than a copy of it.

## What's different from the original

- **Multiple sellers, one marketplace.** Instead of one hard-coded
  `Seller("TechStore")`, any number of sellers can register their own
  store and catalog. `Marketplace` is the shared registry that assembles
  the full product listing across all of them.
- **Quantities, not just yes/no.** The cart is a `Map<Product, Integer>`,
  so buyers can order more than one of an item and adjust quantity from
  the cart screen with a live subtotal.
- **Search, filter, sort.** The buyer view has a keyword search, a
  category filter, and three sort options, all handled by
  `Marketplace.searchAndFilter(...)`.
- **Wishlist**, separate from the cart, with a "move to cart" action.
- **Order history per buyer**, with a coupon code (`SAVE10`, `SAVE20`,
  `WELCOME5`) applied at checkout and three payment methods
  (PayPal, Credit Card, UPI).
- **Admin dashboard.** A third role on the welcome screen, no login
  needed — platform-wide stats (sellers, buyers, products, orders,
  revenue), a table of every seller with the ability to remove one
  (cascades to their listings), a table of every product across every
  seller with moderation removal, and a table of every order ever
  placed with the ability to cancel one.
- **A real color system**, not default JavaFX gray — `styles.css`
  defines an indigo/amber palette, a branded header bar on every
  screen, and a distinct color per product category badge.
- **Customer-facing polish.** Product cards show a category emoji,
  a star rating (or a "New Arrival" tag for anything with no reviews
  yet), and a "🔥 Popular" ribbon on a couple of demo items. The
  marketplace has a promo banner advertising the coupon codes, the
  cart shows trust badges (secure checkout / fast delivery / easy
  returns) above the checkout button, and cards get a subtle
  hover highlight.
- **Each dashboard has its own color identity.** Buyer screens sit on
  a light indigo tint, the seller dashboard on a light amber tint, the
  admin dashboard on a light violet tint. Category quick-filter chips
  (colored pills, one per category) sit above the search bar. Stock
  levels, order status, and category all render as colored pills in
  every table (seller's own product table, and all three admin tables),
  the admin sellers table gets a colored avatar initial per store, and
  the admin revenue figure sits in its own green banner instead of
  plain text.
- **Panes rebuild on navigation.** In the original, the buyer and cart
  panes were built once at startup — before any products existed or any
  buyer had logged in — so they'd show stale/empty data. Here every
  `createXPane()` is called fresh each time you navigate, so it always
  reflects current state.
- **Demo data on launch.** Three sellers with a few products each are
  seeded automatically so the marketplace isn't empty on first run.

## File structure

```
styles.css                 color system — must stay next to src/ when you run the app
src/
  Category.java          enum: product categories
  OrderStatus.java        enum: order lifecycle
  User.java                abstract base for Seller/Buyer
  Product.java
  CartItem.java             product + quantity pairing
  Seller.java
  Buyer.java                cart, wishlist, order history
  PaymentMethod.java       abstract strategy
  PayPal.java / CreditCard.java / UPI.java
  Coupon.java               hard-coded discount codes
  Order.java
  Marketplace.java          central registry + search/filter/sort
  ShopSphereGUI.java        main JavaFX Application (all screens)
```

## How to run

This was written and verified against **OpenJFX 11** (compiles and
launches cleanly with `javac`/`java --module-path`). If your IDE project
uses a different JavaFX version, it should still work unchanged — only
standard, stable controls are used (`BorderPane`, `GridPane`, `TableView`,
`ComboBox`, `Spinner`, `FlowPane`, `ChoiceDialog`, etc.), the same family
of controls as your original file.

**Command line** (adjust the module-path to wherever your JavaFX SDK lives):

```bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -d out src/*.java
java  --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -cp out ShopSphereGUI
```

**IDE (NetBeans / IntelliJ / Eclipse):** create a new JavaFX project, drop
all files from `src/` into the source folder, and run `ShopSphereGUI`
(it has the `main` method).

**About `styles.css`:** the app looks for it in whatever directory you
run `java` from (not a resource baked into the compiled classes), so as
long as you `cd` into the project root before running — same as before —
it'll be found automatically. If it's ever missing, the app still runs
fine, just back in plain default JavaFX gray instead of the indigo/amber
theme.

## Quick tour once it's running

- Landing screen: pick the **Seller** or **Buyer** tab and continue —
  no password, just an ID (reusing the same ID keeps your data across
  a "re-login" within one run).
- As a buyer, browse the seeded catalog right away, search/filter/sort,
  add items to cart or wishlist.
- In the cart, try coupon code `SAVE10` before checking out.
- As a seller, add a product and it appears immediately in the buyer
  marketplace the next time that screen is opened.
- The **Admin** tab needs no ID — it drops straight into a dashboard
  with platform stats and tables to moderate sellers, products, and orders.

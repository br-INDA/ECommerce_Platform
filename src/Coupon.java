/**
 * Tiny hard-coded coupon lookup. Swap this for a database-backed table
 * if you ever extend the project.
 */
public class Coupon {
    public static double getDiscountPercent(String code) {
        if (code == null) {
            return 0.0;
        }
        switch (code.trim().toUpperCase()) {
            case "SAVE10":
                return 0.10;
            case "SAVE20":
                return 0.20;
            case "WELCOME5":
                return 0.05;
            default:
                return 0.0;
        }
    }
}

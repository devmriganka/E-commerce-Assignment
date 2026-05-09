import java.time.LocalDate;

public class Coupon {
    public enum DiscountType { FLAT, PERCENTAGE }

    private String code;
    private DiscountType discountType;
    private double discountValue;
    private LocalDate expiryDate;

    public Coupon(String code, DiscountType discountType, double discountValue, LocalDate expiryDate) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.expiryDate = expiryDate;
    }

    public String getCode() { return code; }
    public double getDiscountValue() { return discountValue; }
    public DiscountType getDiscountType() { return discountType; }

    public boolean isValid(LocalDate currentDate) {
        return currentDate != null && !currentDate.isAfter(expiryDate);
    }

    public double applyDiscount(double subtotal) {
        if (discountType == DiscountType.FLAT) {
            return Math.max(0, subtotal - discountValue);
        } else if (discountType == DiscountType.PERCENTAGE) {
            return Math.max(0, subtotal - (subtotal * discountValue / 100.0));
        }
        return subtotal;
    }
}

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private Coupon appliedCoupon;
    private static final double TAX_RATE = 0.10;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > product.getStockQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
                }
                item.setQuantity(newQuantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public void updateQuantity(String productId, int quantity, Product catalogProduct) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity == 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (catalogProduct != null && quantity > catalogProduct.getStockQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + catalogProduct.getStockQuantity());
        }

        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
        throw new IllegalArgumentException("Item not found in cart");
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void applyCoupon(Coupon coupon, LocalDate currentDate) throws IllegalArgumentException {
        if (coupon == null) {
            throw new IllegalArgumentException("Invalid coupon code");
        }
        if (!coupon.isValid(currentDate)) {
            throw new IllegalArgumentException("Coupon expired");
        }
        this.appliedCoupon = coupon;
    }

    public double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return Utils.roundToTwoDecimals(subtotal);
    }

    public double calculateDiscount() {
        if (appliedCoupon == null) {
            return 0.0;
        }
        double subtotal = calculateSubtotal();
        double afterDiscount = appliedCoupon.applyDiscount(subtotal);
        return Utils.roundToTwoDecimals(subtotal - afterDiscount);
    }

    public double calculateTax() {
        double amountAfterDiscount = calculateSubtotal() - calculateDiscount();
        return Utils.roundToTwoDecimals(amountAfterDiscount * TAX_RATE);
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double discount = calculateDiscount();
        double tax = calculateTax();
        return Utils.roundToTwoDecimals(subtotal - discount + tax);
    }

    public String checkout() throws IllegalStateException {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        // Verify stock again just in case
        for (CartItem item : items) {
            if (item.getQuantity() > item.getProduct().getStockQuantity()) {
                throw new IllegalStateException("Insufficient stock for " + item.getProduct().getName());
            }
        }
        // Deduct stock
        for (CartItem item : items) {
            item.getProduct().reduceStock(item.getQuantity());
        }
        // Generate receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("----- Order Summary -----\n");
        for (CartItem item : items) {
            receipt.append(item.getProduct().getName()).append(" x ").append(item.getQuantity()).append(" = ₹").append(item.getTotalPrice()).append("\n");
        }
        receipt.append("Subtotal: ₹").append(calculateSubtotal()).append("\n");
        if (appliedCoupon != null) {
            receipt.append("Discount applied (").append(appliedCoupon.getCode()).append("): -₹").append(calculateDiscount()).append("\n");
        }
        receipt.append("Tax (10%): ₹").append(calculateTax()).append("\n");
        receipt.append("Total: ₹").append(calculateTotal()).append("\n");
        receipt.append("-------------------------\n");
        receipt.append("Thank you for shopping with us!");
        
        // Clear cart
        items.clear();
        appliedCoupon = null;
        
        return receipt.toString();
    }
    
    public List<CartItem> getItems() {
        return items;
    }
}

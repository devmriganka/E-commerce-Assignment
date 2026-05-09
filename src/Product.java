public class Product {
    private String id;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(String id, String name, double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public void reduceStock(int quantity) {
        if (quantity <= stockQuantity) {
            this.stockQuantity -= quantity;
        }
    }
    
    public void setStockQuantity(int quantity) {
        this.stockQuantity = quantity;
    }

    @Override
    public String toString() {
        return name + " (₹" + price + ")";
    }
}


import java.io.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileHandler {

    public static Map<String, Product> loadProducts(String filePath) {
        Map<String, Product> products = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            boolean hasCategory = false;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().contains("category")) {
                        hasCategory = true;
                    }
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 3) {
                    try {
                        String id = values[0].trim();
                        String name = values[1].trim();
                        double price = 0.0;
                        int stock = 50; // Default stock

                        if (hasCategory && values.length >= 4) {
                            price = Double.parseDouble(values[3].trim());
                            if (values.length >= 5) {
                                stock = Integer.parseInt(values[4].trim());
                            }
                        } else if (!hasCategory && values.length >= 3) {
                            price = Double.parseDouble(values[2].trim());
                            if (values.length >= 4) {
                                stock = Integer.parseInt(values[3].trim());
                            }
                        }
                        products.put(id, new Product(id, name, price, stock));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing numeric value for product: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products file: " + e.getMessage());
        }
        return products;
    }

    public static Map<String, Coupon> loadCoupons(String filePath) {
        Map<String, Coupon> coupons = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String code = values[0].trim();
                    Coupon.DiscountType type = Coupon.DiscountType.valueOf(values[1].trim().toUpperCase());
                    double value = Double.parseDouble(values[2].trim());
                    LocalDate expiry = Utils.parseDate(values[3].trim());
                    coupons.put(code, new Coupon(code, type, value, expiry));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading coupons file: " + e.getMessage());
        }
        return coupons;
    }

    public static void saveProducts(String filePath, Map<String, Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,name,price,stock\n");
            for (Product p : products.values()) {
                bw.write(p.getId() + "," + p.getName() + "," + p.getPrice() + "," + p.getStockQuantity() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing products file: " + e.getMessage());
        }
    }
}

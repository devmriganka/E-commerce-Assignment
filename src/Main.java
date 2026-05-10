import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Product> catalog;
    private static Map<String, Coupon> coupons;
    private static Cart cart;
    private static String productsFile = "data/products.csv";
    private static String couponsFile = "data/coupons.csv";
    private static LocalDate currentDate = LocalDate.now();

    public static void main(String[] args) {
        // Clear the sample_outputs.txt file at the start of execution
        try {
            new java.io.FileOutputStream("output/sample_outputs.txt").close();
        } catch (Exception e) {
            // ignore
        }

        // Load data from CSV
        catalog = FileHandler.loadProducts(productsFile);
        coupons = FileHandler.loadCoupons(couponsFile);
        cart = new Cart();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=============================================");
        System.out.println("   Welcome to the E-Commerce Shopping Cart   ");
        System.out.println("=============================================");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Browse Products");
            System.out.println("2. View Cart");
            System.out.println("3. Manage Cart (Update/Remove Items)");
            System.out.println("4. Apply Coupon");
            System.out.println("5. Checkout");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        browseProducts(scanner);
                        break;
                    case "2":
                        viewCart();
                        break;
                    case "3":
                        manageCart(scanner);
                        break;
                    case "4":
                        applyCoupon(scanner);
                        break;
                    case "5":
                        if (checkout()) {
                            // After successful checkout, update the inventory in the CSV file
                            FileHandler.saveProducts(productsFile, catalog);
                        }
                        break;
                    case "6":
                        running = false;
                        System.out.println("Thank you for shopping with us! Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 6.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (running) {
                System.out.print("\nPress Enter to return to the main menu...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void browseProducts(Scanner scanner) {
        System.out.println("\n--- Product Catalog ---");
        System.out.printf("%-5s | %-15s | %-10s | %-5s\n", "ID", "Name", "Price", "Stock");
        System.out.println("--------------------------------------------------");
        for (Product p : catalog.values()) {
            System.out.printf("%-5s | %-15s | ₹%-9.2f | %-5d\n", p.getId(), p.getName(), p.getPrice(), p.getStockQuantity());
        }
        System.out.print("\nWould you like to add an item to your cart now? (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        if (choice.equals("Y") || choice.equals("YES")) {
            addToCart(scanner);
        }
    }

    private static String formatProductId(String input) {
        if (input.matches("P?\\d+")) {
            String numStr = input.startsWith("P") ? input.substring(1) : input;
            return String.format("P%03d", Integer.parseInt(numStr));
        }
        return input;
    }

    private static void addToCart(Scanner scanner) {
        boolean addMore = true;
        while (addMore) {
            System.out.print("Enter Product ID (e.g., 1 or P001): ");
            String id = formatProductId(scanner.nextLine().trim().toUpperCase());
            if (!catalog.containsKey(id)) {
                System.out.println("Product not found.");
            } else {
                System.out.print("Enter Quantity: ");
                int qty;
                try {
                    qty = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity format.");
                    // Ask if they want to try again after invalid input
                    System.out.print("Would you like to try again? (Y/N): ");
                    String tryAgain = scanner.nextLine().trim().toUpperCase();
                    if (!tryAgain.equals("Y") && !tryAgain.equals("YES")) {
                        addMore = false;
                    }
                    continue; // Skip the rest and ask if they want to add more
                }
                cart.addItem(catalog.get(id), qty);
                System.out.println("Item added to cart successfully.");
            }

            // Ask if they want to add more items
            System.out.print("Would you like to add more items? (Y/N): ");
            String choice = scanner.nextLine().trim().toUpperCase();
            addMore = choice.equals("Y") || choice.equals("YES");
        }
    }

    private static void viewCart() {
        System.out.println("\n--- Your Cart ---");
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        for (CartItem item : cart.getItems()) {
            System.out.println(item.getProduct().getName() + " (ID: " + item.getProduct().getId() + ") x " + item.getQuantity() + " = ₹" + item.getTotalPrice());
        }
        System.out.println("-------------------------");
        System.out.println("Subtotal: ₹" + cart.calculateSubtotal());
        System.out.println("Discount: -₹" + cart.calculateDiscount());
        System.out.println("Tax (10%): ₹" + cart.calculateTax());
        System.out.println("Total: ₹" + cart.calculateTotal());
    }

    private static void updateQuantity(Scanner scanner) {
        System.out.print("Enter Product ID to update: ");
        String id = formatProductId(scanner.nextLine().trim().toUpperCase());
        System.out.print("Enter New Quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity format.");
            return;
        }
        cart.updateQuantity(id, qty, catalog.get(id));
        System.out.println("Quantity updated successfully.");
    }

    private static void removeItem(Scanner scanner) {
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n--- Your Cart ---");
        List<CartItem> items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            System.out.println((i + 1) + ". " + item.getProduct().getName() + " (ID: " + item.getProduct().getId() + ") x " + item.getQuantity());
        }
        System.out.println("-------------------------");

        System.out.print("Enter item number to remove: ");
        String input = scanner.nextLine().trim();
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < items.size()) {
                String idToRemove = items.get(index).getProduct().getId();
                cart.removeItem(idToRemove);
                System.out.println("Item removed successfully.");
            } else {
                System.out.println("Invalid item number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void applyCoupon(Scanner scanner) {
        System.out.println("\n--- Available Coupons ---");
        int index = 1;
        List<String> couponCodes = new ArrayList<>(coupons.keySet());
        for (String code : couponCodes) {
            Coupon c = coupons.get(code);
            String discountStr = c.getDiscountType() == Coupon.DiscountType.FLAT 
                ? "₹" + c.getDiscountValue() + " OFF" 
                : c.getDiscountValue() + "% OFF";
            System.out.println(index + ". " + c.getCode() + " (" + discountStr + ")");
            index++;
        }
        System.out.println("-------------------------");
        
        System.out.print("Enter Coupon Code or Number: ");
        String input = scanner.nextLine().trim().toUpperCase();
        String selectedCode = input;
        
        if (input.matches("\\d+")) {
            int selectedIndex = Integer.parseInt(input) - 1;
            if (selectedIndex >= 0 && selectedIndex < couponCodes.size()) {
                selectedCode = couponCodes.get(selectedIndex);
            }
        }

        if (!coupons.containsKey(selectedCode)) {
            System.out.println("Error: Invalid coupon selection.");
            return;
        }
        cart.applyCoupon(coupons.get(selectedCode), currentDate);
        System.out.println("Coupon applied successfully!");
    }

    private static void manageCart(Scanner scanner) {
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Manage Cart ---");
            System.out.println("1. Update Item Quantity");
            System.out.println("2. Remove Item from Cart");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    updateQuantity(scanner);
                    break;
                case "2":
                    removeItem(scanner);
                    break;
                case "3":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }

            if (managing) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private static boolean checkout() {
        try {
            String receipt = cart.checkout();
            System.out.println(receipt);
            System.out.println("Checkout successful! Your inventory has been updated.");
            
            // Write receipt to sample_outputs.txt
            try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileOutputStream("output/sample_outputs.txt", true))) {
                out.println(receipt);
                out.println();
            } catch (Exception e) {
                System.out.println("Could not write receipt to file.");
            }
            
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Checkout failed: " + e.getMessage());
            return false;
        }
    }
}

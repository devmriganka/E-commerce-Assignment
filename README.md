# E-Commerce Shopping Cart System

## Description
A robust E-Commerce Shopping Cart System built in Java that manages product inventory, handles cart operations (add/update/remove), calculates totals with tax and coupon discounts, and persists inventory updates to CSV files. The application provides a user-friendly, interactive command-line interface that allows users to navigate the system, manage their shopping carts, and perform checkouts.

## Features
- **Product Inventory Management:** Read products from CSV, display available items, and update inventory upon checkout.
- **Cart Operations:** 
  - Add products to the cart.
  - Update the quantity of items in the cart.
  - Remove items from the cart.
  - View current cart contents.
- **Checkout Process:**
  - Automatic calculation of subtotal, tax (8%), and final total.
  - Support for applying coupon codes for discounts.
  - Inventory persistence to CSV files after a successful checkout.
- **Interactive CLI:** Clear navigation menus and prompts for a seamless user experience.

## Project Structure
- `src/`: Contains all the Java source code (`Main.java`, `Cart.java`, `Product.java`, `CartItem.java`, `Coupon.java`, `FileHandler.java`, `Utils.java`).
- `data/`: Contains the CSV files used for data persistence (`products.csv`, `coupons.csv`).
- `bin/`: (Optional) Output directory for compiled `.class` files.
- `output/`: Directory for any generated output logs or receipts.

## How to Run
1. Make sure you have Java Development Kit (JDK) installed.
2. Navigate to the project root directory.
3. Compile the Java files:
   ```bash
   javac -d bin src/*.java
   ```
4. Run the application:
   ```bash
   java -cp bin Main
   ```

## Requirements
- Java 8 or higher.

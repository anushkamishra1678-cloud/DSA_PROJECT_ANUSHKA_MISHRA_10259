// ONLINE SHOPPING CART SYSTEM
import java.io.*;
import java.util.*;

// Product Model for HashMap Catalog
class Product {
    String id;
    String name;
    int price;

    public Product(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

// Custom Singly Linked List Node for Cart
class CartNode {
    String productId;
    String name;
    int unitPrice;
    int quantity;
    CartNode next;

    public CartNode(String productId, String name, int unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.next = null;
    }

    public int getItemTotal() {
        return unitPrice * quantity;
    }
}

// Custom Linked List Implementation for Shopping Cart
class CartLinkedList {
    CartNode head;

    // Add item to end of linked list (or update quantity if already present)
    public void addOrUpdate(String id, String name, int price, int qty) {
        if (head == null) {
            head = new CartNode(id, name, price, qty);
            return;
        }
        CartNode curr = head;
        while (curr != null) {
            if (curr.productId.equals(id)) {
                curr.quantity += qty;
                return;
            }
            if (curr.next == null) break;
            curr = curr.next;
        }
        curr.next = new CartNode(id, name, price, qty);
    }

    // Undo/Remove last added action using Stack info
    public boolean undoLastAddition(String id, int qtyToRemove) {
        if (head == null) return false;

        CartNode curr = head;
        CartNode prev = null;

        while (curr != null) {
            if (curr.productId.equals(id)) {
                if (curr.quantity > qtyToRemove) {
                    curr.quantity -= qtyToRemove;
                } else {
                    if (prev == null) {
                        head = curr.next;
                    } else {
                        prev.next = curr.next;
                    }
                }
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    // Sort Linked List by item total (High to Low) using Bubble Sort
    public void sortHighToLow() {
        if (head == null || head.next == null) return;

        boolean swapped;
        do {
            swapped = false;
            CartNode curr = head;
            while (curr.next != null) {
                if (curr.getItemTotal() < curr.next.getItemTotal()) {
                    String tempId = curr.productId;
                    String tempName = curr.name;
                    int tempPrice = curr.unitPrice;
                    int tempQty = curr.quantity;

                    curr.productId = curr.next.productId;
                    curr.name = curr.next.name;
                    curr.unitPrice = curr.next.unitPrice;
                    curr.quantity = curr.next.quantity;

                    curr.next.productId = tempId;
                    curr.next.name = tempName;
                    curr.next.unitPrice = tempPrice;
                    curr.next.quantity = tempQty;

                    swapped = true;
                }
                curr = curr.next;
            }
        } while (swapped);
    }

    public int calculateGrandTotal() {
        int total = 0;
        CartNode curr = head;
        while (curr != null) {
            total += curr.getItemTotal();
            curr = curr.next;
        }
        return total;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void clear() {
        head = null;
    }
}

// Helper record for Stack (tracks last added item & quantity)
class CartAction {
    String productId;
    String productName;
    int quantity;

    public CartAction(String productId, String productName, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }
}

public class Project {
    private static final String FILE_NAME = "products.txt";
    private Map<String, Product> productCatalog = new LinkedHashMap<>();
    private CartLinkedList cart = new CartLinkedList();
    private Stack<CartAction> actionStack = new Stack<>();

    // Load products from file on startup (creates default file if missing)
    public void loadProducts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            productCatalog.put("P101", new Product("P101", "Notebook", 50));
            productCatalog.put("P102", new Product("P102", "Pen", 10));
            productCatalog.put("P103", new Product("P103", "Bag", 800));
            saveProducts();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> parts = Arrays.asList(line.split(","));
                if (parts.size() == 3) {
                    String id = parts.get(0).trim();
                    String name = parts.get(1).trim();
                    int price = Integer.parseInt(parts.get(2).trim());
                    productCatalog.put(id, new Product(id, name, price));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    // Save products back to file on exit
    public void saveProducts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product p : productCatalog.values()) {
                bw.write(p.id + "," + p.name + "," + p.price);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    // Option 1: View Products
    public void viewProducts() {
        System.out.println("\n--- Available Products ---");
        for (Product p : productCatalog.values()) {
            System.out.println(p.id + " | " + p.name + " | " + p.price);
        }
    }

    // Option 2: Add to Cart
    public void addToCart(String prodId, int qty) {
        if (!productCatalog.containsKey(prodId)) {
            System.out.println("Product " + prodId + " not found.");
            return;
        }
        if (qty <= 0) {
            System.out.println("Quantity must be a positive whole number.");
            return;
        }

        Product p = productCatalog.get(prodId);
        cart.addOrUpdate(p.id, p.name, p.price, qty);
        actionStack.push(new CartAction(p.id, p.name, qty));

        System.out.println("Added: " + p.name + " x" + qty + " = " + (p.price * qty));
    }

    // Option 3: View Cart
    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            System.out.println("Total: 0");
            return;
        }

        CartNode curr = cart.head;
        int index = 1;
        while (curr != null) {
            System.out.println(index + ". " + curr.name + " | " + curr.getItemTotal());
            curr = curr.next;
            index++;
        }
        System.out.println("Total: " + cart.calculateGrandTotal());
    }

    // Option 4: Remove Last Added Item (Using Stack)
    public void removeLastItem() {
        if (actionStack.isEmpty() || cart.isEmpty()) {
            System.out.println("Cart is empty. Nothing to remove.");
            return;
        }

        CartAction lastAction = actionStack.pop();
        cart.undoLastAddition(lastAction.productId, lastAction.quantity);
        System.out.println("Removed last added: " + lastAction.productName + " x" + lastAction.quantity);
    }

    // Option 5: Sort Cart (by item total, high to low)
    public void sortCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            System.out.println("Total: 0");
            return;
        }

        cart.sortHighToLow();
        CartNode curr = cart.head;
        int index = 1;
        while (curr != null) {
            System.out.println(index + ". " + curr.name + " | " + curr.getItemTotal());
            curr = curr.next;
            index++;
        }
    }

    // Option 6: Checkout
    public void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            System.out.println("Total: 0");
            return;
        }

        int total = cart.calculateGrandTotal();
        System.out.println("Order placed. Total: " + total);
        cart.clear();
        actionStack.clear();
    }

    public static void main(String[] args) {
        Project app = new Project();
        app.loadProducts();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ONLINE SHOPPING CART SYSTEM ===");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Remove Last Added Item");
            System.out.println("5. Sort Cart (High to Low)");
            System.out.println("6. Checkout");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number (1-7).");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> app.viewProducts();
                case 2 -> {
                    System.out.print("Product ID: ");
                    String id = scanner.next().trim().toUpperCase();
if (!app.productCatalog.containsKey(id)) {
System.out.println("Product " + id + " not found.");
} else {
System.out.print("Quantity: ");
if (!scanner.hasNextInt()) {
System.out.println("Quantity must be a positive whole number.");
scanner.next();
} else {
int qty = scanner.nextInt();
app.addToCart(id, qty);
}
}
}
case 3 -> app.viewCart();
case 4 -> app.removeLastItem();
case 5 -> app.sortCart();
case 6 -> app.checkout();
case 7 -> {
app.saveProducts();
System.out.println("Data saved. Exiting...");
scanner.close();
System.exit(0);
}
default -> System.out.println("Invalid choice. Select between 1 and 7.");
}
}
}
}
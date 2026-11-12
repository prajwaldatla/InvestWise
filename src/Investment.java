import java.text.NumberFormat;
import java.util.Locale;

/**
 * Abstract parent class representing a generic financial investment.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class Investment {
    private String id;
    private String name;
    private double quantity;
    private double purchasePrice;
    private double currentPrice;
    private String purchaseDate;

    public Investment(String id, String name, double quantity, double purchasePrice, double currentPrice, String purchaseDate) {
        setId(id);
        setName(name);
        setQuantity(quantity);
        setPurchasePrice(purchasePrice);
        setCurrentPrice(currentPrice);
        setPurchaseDate(purchaseDate);
    }

    // Abstract methods demonstrating Polymorphism in subclasses
    public abstract String getInvestmentType();
    public abstract String getSpecificDetails();

    // Financial Calculations (Module B)
    public double getInitialInvestment() {
        return quantity * purchasePrice;
    }

    public double getCurrentValue() {
        return quantity * currentPrice;
    }

    public double getProfitOrLoss() {
        return getCurrentValue() - getInitialInvestment();
    }

    public double getReturnPercentage() {
        double initial = getInitialInvestment();
        if (initial == 0) {
            return 0.0;
        }
        return (getProfitOrLoss() / initial) * 100.0;
    }

    // Encapsulated Getters and Validated Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Investment ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Investment name cannot be empty.");
        }
        this.name = name.trim();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity or units must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        if (purchasePrice <= 0) {
            throw new IllegalArgumentException("Purchase price must be greater than zero.");
        }
        this.purchasePrice = purchasePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        if (currentPrice < 0) {
            throw new IllegalArgumentException("Current price cannot be negative.");
        }
        this.currentPrice = currentPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        if (purchaseDate == null || purchaseDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Purchase date cannot be empty.");
        }
        this.purchaseDate = purchaseDate.trim();
    }

    public static String formatINR(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(amount);
    }
}
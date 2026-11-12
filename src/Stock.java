/**
 * Represents an individual equity stock investment.
 * Demonstrates Inheritance and Polymorphism.
 */
public class Stock extends Investment {
    private String tickerSymbol;

    public Stock(String id, String name, double quantity, double purchasePrice, double currentPrice, String purchaseDate, String tickerSymbol) {
        super(id, name, quantity, purchasePrice, currentPrice, purchaseDate);
        this.tickerSymbol = (tickerSymbol == null || tickerSymbol.trim().isEmpty()) ? "NSE" : tickerSymbol.trim().toUpperCase();
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = (tickerSymbol == null || tickerSymbol.trim().isEmpty()) ? "NSE" : tickerSymbol.trim().toUpperCase();
    }

    @Override
    public String getInvestmentType() {
        return "Stock";
    }

    @Override
    public String getSpecificDetails() {
        return "Ticker: " + tickerSymbol;
    }
}
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Mutual Fund investment and stores its underlying stock holdings.
 * Demonstrates Inheritance, Polymorphism, and Java Collections (HashSet).
 */
public class MutualFund extends Investment {
    private String fundCategory;
    private Set<String> holdings;

    public MutualFund(String id, String name, double units, double purchaseNav, double currentNav, String purchaseDate, String fundCategory) {
        super(id, name, units, purchaseNav, currentNav, purchaseDate);
        this.fundCategory = (fundCategory == null || fundCategory.trim().isEmpty()) ? "Equity" : fundCategory.trim();
        this.holdings = new HashSet<>();
    }

    public String getFundCategory() {
        return fundCategory;
    }

    public void setFundCategory(String fundCategory) {
        this.fundCategory = (fundCategory == null || fundCategory.trim().isEmpty()) ? "Equity" : fundCategory.trim();
    }

    public Set<String> getHoldings() {
        return new HashSet<>(holdings);
    }

    public void setHoldings(Set<String> newHoldings) {
        this.holdings.clear();
        if (newHoldings != null) {
            for (String stock : newHoldings) {
                addHolding(stock);
            }
        }
    }

    public void addHolding(String stockName) {
        if (stockName != null && !stockName.trim().isEmpty()) {
            this.holdings.add(stockName.trim());
        }
    }

    @Override
    public String getInvestmentType() {
        return "Mutual Fund";
    }

    @Override
    public String getSpecificDetails() {
        return "Category: " + fundCategory + " (" + holdings.size() + " holdings)";
    }
}
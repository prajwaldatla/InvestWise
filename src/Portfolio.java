import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages a collection of Investment objects.
 * Demonstrates Composition (Portfolio HAS-A List of Investments).
 */
public class Portfolio {
    private List<Investment> investments;

    public Portfolio() {
        this.investments = new ArrayList<>();
    }

    public void addInvestment(Investment investment) {
        if (investment == null) {
            throw new IllegalArgumentException("Cannot add a null investment.");
        }
        if (findById(investment.getId()) != null) {
            throw new IllegalArgumentException("An investment with ID '" + investment.getId() + "' already exists.");
        }
        investments.add(investment);
    }

    public boolean removeInvestment(String id) {
        Investment existing = findById(id);
        if (existing != null) {
            return investments.remove(existing);
        }
        return false;
    }

    public Investment findById(String id) {
        for (Investment inv : investments) {
            if (inv.getId().equalsIgnoreCase(id)) {
                return inv;
            }
        }
        return null;
    }

    public boolean isDuplicateRecord(String name, String type, String purchaseDate, double purchasePrice) {
        for (Investment inv : investments) {
            boolean sameName = inv.getName().equalsIgnoreCase(name.trim());
            boolean sameType = inv.getInvestmentType().equalsIgnoreCase(type.trim());
            boolean sameDate = inv.getPurchaseDate().equalsIgnoreCase(purchaseDate.trim());
            boolean samePrice = Math.abs(inv.getPurchasePrice() - purchasePrice) < 0.001;
            if (sameName && sameType && sameDate && samePrice) {
                return true;
            }
        }
        return false;
    }

    public List<Investment> getAllInvestments() {
        return new ArrayList<>(investments);
    }

    public void setAllInvestments(List<Investment> newInvestments) {
        this.investments = new ArrayList<>(newInvestments);
    }

    public List<Investment> searchAndFilter(String searchText, String typeFilter) {
        List<Investment> result = new ArrayList<>();
        String query = (searchText == null) ? "" : searchText.trim().toLowerCase();
        String filter = (typeFilter == null) ? "All" : typeFilter.trim();

        for (Investment inv : investments) {
            boolean matchesType = filter.equalsIgnoreCase("All") || inv.getInvestmentType().equalsIgnoreCase(filter);
            boolean matchesQuery = query.isEmpty()
                    || inv.getName().toLowerCase().contains(query)
                    || inv.getId().toLowerCase().contains(query)
                    || inv.getSpecificDetails().toLowerCase().contains(query);

            if (matchesType && matchesQuery) {
                result.add(inv);
            }
        }
        return result;
    }

    public String generateNextId(String type) {
        String prefix = type.equalsIgnoreCase("Stock") ? "STK-" : "MF-";
        int maxNumber = 100;
        for (Investment inv : investments) {
            if (inv.getId().toUpperCase().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(inv.getId().substring(prefix.length()));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore custom user IDs that do not end with numbers
                }
            }
        }
        return prefix + (maxNumber + 1);
    }

    // Module C: Asset Allocation Calculation
    public Map<String, Double> getAssetAllocationByType() {
        Map<String, Double> allocation = new LinkedHashMap<>();
        allocation.put("Stock", 0.0);
        allocation.put("Mutual Fund", 0.0);

        for (Investment inv : investments) {
            String type = inv.getInvestmentType();
            double currentVal = allocation.getOrDefault(type, 0.0);
            allocation.put(type, currentVal + inv.getCurrentValue());
        }
        return allocation;
    }

    public Map<String, Double> getAssetAllocationPercentages() {
        Map<String, Double> values = getAssetAllocationByType();
        Map<String, Double> percentages = new LinkedHashMap<>();
        double totalValue = ReturnCalculator.calculateCurrentPortfolioValue(investments);

        for (Map.Entry<String, Double> entry : values.entrySet()) {
            if (totalValue > 0) {
                percentages.put(entry.getKey(), (entry.getValue() / totalValue) * 100.0);
            } else {
                percentages.put(entry.getKey(), 0.0);
            }
        }
        return percentages;
    }
}
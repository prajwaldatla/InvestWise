import java.util.List;

/**
 * Utility class for portfolio-wide profit, loss, and return calculations (Module B).
 */
public class ReturnCalculator {

    public static double calculateTotalInvested(List<Investment> investments) {
        double total = 0.0;
        if (investments == null) return total;
        for (Investment inv : investments) {
            total += inv.getInitialInvestment();
        }
        return total;
    }

    public static double calculateCurrentPortfolioValue(List<Investment> investments) {
        double total = 0.0;
        if (investments == null) return total;
        for (Investment inv : investments) {
            total += inv.getCurrentValue();
        }
        return total;
    }

    public static double calculateTotalProfitOrLoss(List<Investment> investments) {
        return calculateCurrentPortfolioValue(investments) - calculateTotalInvested(investments);
    }

    public static double calculateOverallReturnPercentage(List<Investment> investments) {
        double totalInvested = calculateTotalInvested(investments);
        if (totalInvested == 0.0) {
            return 0.0;
        }
        double totalProfitOrLoss = calculateTotalProfitOrLoss(investments);
        return (totalProfitOrLoss / totalInvested) * 100.0;
    }
}
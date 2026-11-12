import java.util.ArrayList;
import java.util.List;

/**
 * Performs statistical risk analysis on historical stock prices (Module E).
 * Uses sample variance (dividing by n - 1) because the historical prices
 * represent a finite sample of trading days rather than the entire population.
 */
public class RiskAnalyzer {

    public static class PricePoint {
        private final String date;
        private final double closePrice;

        public PricePoint(String date, double closePrice) {
            this.date = date;
            this.closePrice = closePrice;
        }

        public String getDate() { return date; }
        public double getClosePrice() { return closePrice; }
    }

    /**
     * Daily Return = (Today's Price - Previous Day's Price) / Previous Day's Price
     */
    public static List<Double> calculateDailyReturns(List<PricePoint> prices) {
        List<Double> returns = new ArrayList<>();
        if (prices == null || prices.size() < 2) {
            return returns;
        }

        for (int i = 1; i < prices.size(); i++) {
            double prevPrice = prices.get(i - 1).getClosePrice();
            double todayPrice = prices.get(i).getClosePrice();
            if (prevPrice <= 0) {
                throw new IllegalArgumentException("Historical price must be positive on date: " + prices.get(i - 1).getDate());
            }
            double dailyReturn = (todayPrice - prevPrice) / prevPrice;
            returns.add(dailyReturn);
        }
        return returns;
    }

    /**
     * Calculates the arithmetic mean of daily returns.
     */
    public static double calculateMeanReturn(List<Double> dailyReturns) {
        if (dailyReturns == null || dailyReturns.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double r : dailyReturns) {
            sum += r;
        }
        return sum / dailyReturns.size();
    }

    /**
     * Calculates Sample Variance: s^2 = sum((r_i - mean)^2) / (n - 1)
     */
    public static double calculateSampleVariance(List<Double> dailyReturns) {
        if (dailyReturns == null || dailyReturns.size() < 2) {
            return 0.0;
        }
        double mean = calculateMeanReturn(dailyReturns);
        double squaredDiffSum = 0.0;
        for (double r : dailyReturns) {
            double diff = r - mean;
            squaredDiffSum += diff * diff;
        }
        return squaredDiffSum / (dailyReturns.size() - 1);
    }

    /**
     * Standard Deviation = sqrt(Sample Variance)
     */
    public static double calculateStandardDeviation(List<Double> dailyReturns) {
        return Math.sqrt(calculateSampleVariance(dailyReturns));
    }
}
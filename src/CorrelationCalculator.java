import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates the Pearson Correlation Coefficient between two stocks (Module F).
 * Formula: r = Cov(X, Y) / (StdDev(X) * StdDev(Y))
 */
public class CorrelationCalculator {

    public static class CorrelationResult {
        private final String stockA;
        private final String stockB;
        private final int alignedPriceCount;
        private final int returnObservations;
        private final double correlation;
        private final String interpretation;

        public CorrelationResult(String stockA, String stockB, int alignedPriceCount, int returnObservations, double correlation, String interpretation) {
            this.stockA = stockA;
            this.stockB = stockB;
            this.alignedPriceCount = alignedPriceCount;
            this.returnObservations = returnObservations;
            this.correlation = correlation;
            this.interpretation = interpretation;
        }

        public String getStockA() { return stockA; }
        public String getStockB() { return stockB; }
        public int getAlignedPriceCount() { return alignedPriceCount; }
        public int getReturnObservations() { return returnObservations; }
        public double getCorrelation() { return correlation; }
        public String getInterpretation() { return interpretation; }
    }

    /**
     * Aligns two stock price histories by date and computes Pearson correlation on daily returns.
     */
    public static CorrelationResult analyzeCorrelation(String stockAName, List<RiskAnalyzer.PricePoint> pricesA,
                                                       String stockBName, List<RiskAnalyzer.PricePoint> pricesB) {
        if (pricesA == null || pricesB == null) {
            throw new IllegalArgumentException("Historical price lists cannot be null.");
        }

        // Map dates to prices for Stock B for O(1) lookup
        Map<String, Double> mapB = new LinkedHashMap<>();
        for (RiskAnalyzer.PricePoint pt : pricesB) {
            if (pt.getClosePrice() > 0) {
                mapB.put(pt.getDate(), pt.getClosePrice());
            }
        }

        // Align dates present in both Stock A and Stock B
        List<RiskAnalyzer.PricePoint> alignedA = new ArrayList<>();
        List<RiskAnalyzer.PricePoint> alignedB = new ArrayList<>();

        for (RiskAnalyzer.PricePoint ptA : pricesA) {
            if (ptA.getClosePrice() > 0 && mapB.containsKey(ptA.getDate())) {
                alignedA.add(ptA);
                alignedB.add(new RiskAnalyzer.PricePoint(ptA.getDate(), mapB.get(ptA.getDate())));
            }
        }

        if (alignedA.size() < 3) {
            throw new IllegalArgumentException("At least 3 common trading dates are required to calculate correlation.");
        }

        List<Double> returnsX = RiskAnalyzer.calculateDailyReturns(alignedA);
        List<Double> returnsY = RiskAnalyzer.calculateDailyReturns(alignedB);

        double r = calculatePearsonCorrelation(returnsX, returnsY);
        String interpretation = interpretCorrelation(r);

        return new CorrelationResult(stockAName, stockBName, alignedA.size(), returnsX.size(), r, interpretation);
    }

    public static double calculatePearsonCorrelation(List<Double> x, List<Double> y) {
        if (x == null || y == null || x.size() != y.size() || x.size() < 2) {
            throw new IllegalArgumentException("Return series must have the same length and at least 2 observations.");
        }

        int n = x.size();
        double meanX = RiskAnalyzer.calculateMeanReturn(x);
        double meanY = RiskAnalyzer.calculateMeanReturn(y);

        double covarianceSum = 0.0;
        for (int i = 0; i < n; i++) {
            covarianceSum += (x.get(i) - meanX) * (y.get(i) - meanY);
        }

        // Sample Covariance = sum((x_i - meanX)*(y_i - meanY)) / (n - 1)
        double sampleCovariance = covarianceSum / (n - 1);
        double stdDevX = RiskAnalyzer.calculateStandardDeviation(x);
        double stdDevY = RiskAnalyzer.calculateStandardDeviation(y);

        if (stdDevX == 0.0 || stdDevY == 0.0) {
            throw new ArithmeticException("Cannot calculate correlation: One of the stocks has zero price variation (zero standard deviation).");
        }

        double r = sampleCovariance / (stdDevX * stdDevY);
        // Clamp minor floating-point inaccuracies to [-1.0, 1.0]
        return Math.max(-1.0, Math.min(1.0, r));
    }

    public static String interpretCorrelation(double r) {
        if (r >= 0.70) {
            return "Strong Positive Correlation: Both stocks tend to move in the same direction on most trading days. Holding both offers limited diversification.";
        } else if (r >= 0.30) {
            return "Moderate Positive Correlation: The stocks often move together, though independent company factors also influence their returns.";
        } else if (r > -0.30) {
            return "Low / Weak Linear Correlation: The two stocks move relatively independently of each other, which helps diversify portfolio risk.";
        } else if (r > -0.70) {
            return "Moderate Negative Correlation: When one stock rises, the other tends to decline, providing a natural hedge in the portfolio.";
        } else {
            return "Strong Negative Correlation: The stocks move strongly in opposite directions.";
        }
    }
}
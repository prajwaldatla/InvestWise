import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Automated verification test suite covering all 13 functional and mathematical test cases
 * plus CSV Import validation.
 * Run with: java -cp out InvestWiseTest
 */
public class InvestWiseTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("  INVESTWISE AUTOMATED TEST SUITE (14 TEST CASES)");
        System.out.println("=========================================================");

        test1_AddInvestment();
        test2_DeleteInvestment();
        test3_EditInvestment();
        test4_CalculateProfit();
        test5_CalculateLoss();
        test6_CalculateReturnPercentage();
        test7_AssetAllocation();
        test8_OverlapDetection();
        test9_VarianceAndStdDev();
        test10_CorrelationCalculation();
        test11_EmptyPortfolioHandling();
        test12_InvalidInputHandling();
        test13_CsvLoadAndSave();
        test14_UserCsvImportValidation();

        System.out.println("---------------------------------------------------------");
        System.out.println("  RESULTS: " + passed + " Passed, " + failed + " Failed");
        System.out.println("=========================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertCondition(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            passed++;
        } else {
            System.out.println("[FAIL] " + testName);
            failed++;
        }
    }

    private static void test1_AddInvestment() {
        Portfolio p = new Portfolio();
        Stock s = new Stock("STK-1", "Reliance", 10, 2000, 2500, "2025-01-01", "RELIANCE");
        p.addInvestment(s);
        assertCondition("Test 1: Add Investment to Portfolio", p.getAllInvestments().size() == 1 && p.findById("STK-1") != null);
    }

    private static void test2_DeleteInvestment() {
        Portfolio p = new Portfolio();
        p.addInvestment(new Stock("STK-1", "Reliance", 10, 2000, 2500, "2025-01-01", "RELIANCE"));
        boolean removed = p.removeInvestment("STK-1");
        assertCondition("Test 2: Delete Investment from Portfolio", removed && p.getAllInvestments().isEmpty());
    }

    private static void test3_EditInvestment() {
        Stock s = new Stock("STK-1", "Reliance", 10, 2000, 2500, "2025-01-01", "RELIANCE");
        s.setQuantity(20);
        s.setCurrentPrice(2600);
        assertCondition("Test 3: Edit Investment Details", s.getQuantity() == 20 && s.getCurrentValue() == 52000.0);
    }

    private static void test4_CalculateProfit() {
        Stock s = new Stock("STK-1", "TCS", 10, 3000, 3500, "2025-01-01", "TCS");
        // Initial = 30,000, Current = 35,000, Profit = +5,000
        assertCondition("Test 4: Calculate Profit (+₹5,000)", Math.abs(s.getProfitOrLoss() - 5000.0) < 0.001);
    }

    private static void test5_CalculateLoss() {
        Stock s = new Stock("STK-2", "Infosys", 20, 1500, 1400, "2025-01-01", "INFY");
        // Initial = 30,000, Current = 28,000, Loss = -2,000
        assertCondition("Test 5: Calculate Loss (-₹2,000)", Math.abs(s.getProfitOrLoss() - (-2000.0)) < 0.001);
    }

    private static void test6_CalculateReturnPercentage() {
        Stock s = new Stock("STK-1", "TCS", 10, 2000, 2500, "2025-01-01", "TCS");
        // Profit = 5000 on 20000 = +25.0%
        assertCondition("Test 6: Calculate Return Percentage (25.0%)", Math.abs(s.getReturnPercentage() - 25.0) < 0.001);
    }

    private static void test7_AssetAllocation() {
        Portfolio p = new Portfolio();
        p.addInvestment(new Stock("STK-1", "Reliance", 10, 100, 600, "2025-01-01", "REL")); // Current = 6000 (60%)
        p.addInvestment(new MutualFund("MF-1", "Axis Fund", 10, 100, 400, "2025-01-01", "Equity")); // Current = 4000 (40%)
        Map<String, Double> pct = p.getAssetAllocationPercentages();
        boolean ok = Math.abs(pct.get("Stock") - 60.0) < 0.01 && Math.abs(pct.get("Mutual Fund") - 40.0) < 0.01;
        assertCondition("Test 7: Asset Allocation Percentages (60% Stock / 40% MF)", ok);
    }

    private static void test8_OverlapDetection() {
        Set<String> fundA = new HashSet<>(Arrays.asList("Reliance Industries", "TCS", "Infosys", "HDFC Bank"));
        Set<String> fundB = new HashSet<>(Arrays.asList("Reliance Industries", "TCS", "ICICI Bank", "Wipro"));
        OverlapDetector.OverlapResult res = OverlapDetector.analyzeOverlap("Fund A", fundA, "Fund B", fundB);
        boolean ok = res.getCommonCount() == 2 && Math.abs(res.getOverlapRelativeToA() - 50.0) < 0.01;
        assertCondition("Test 8: Mutual Fund Overlap Detection (2 common, 50% overlap)", ok);
    }

    private static void test9_VarianceAndStdDev() {
        // Sample returns: 0.02, 0.04, 0.06 -> Mean = 0.04
        // Sample Variance = ((0.02-0.04)^2 + 0 + (0.06-0.04)^2) / (3 - 1) = 0.0008 / 2 = 0.0004
        // Std Dev = sqrt(0.0004) = 0.02
        List<Double> sampleReturns = Arrays.asList(0.02, 0.04, 0.06);
        double mean = RiskAnalyzer.calculateMeanReturn(sampleReturns);
        double var = RiskAnalyzer.calculateSampleVariance(sampleReturns);
        double sd = RiskAnalyzer.calculateStandardDeviation(sampleReturns);
        boolean ok = Math.abs(mean - 0.04) < 1e-6 && Math.abs(var - 0.0004) < 1e-6 && Math.abs(sd - 0.02) < 1e-6;
        assertCondition("Test 9: Sample Variance (0.0004) & Standard Deviation (0.02)", ok);
    }

    private static void test10_CorrelationCalculation() {
        List<Double> x = Arrays.asList(0.01, 0.02, 0.03, 0.04);
        List<Double> yPerfectPos = Arrays.asList(0.02, 0.04, 0.06, 0.08);
        List<Double> yPerfectNeg = Arrays.asList(0.08, 0.06, 0.04, 0.02);
        double rPos = CorrelationCalculator.calculatePearsonCorrelation(x, yPerfectPos);
        double rNeg = CorrelationCalculator.calculatePearsonCorrelation(x, yPerfectNeg);
        assertCondition("Test 10: Pearson Correlation (+1.0 and -1.0 verification)",
                Math.abs(rPos - 1.0) < 1e-6 && Math.abs(rNeg - (-1.0)) < 1e-6);
    }

    private static void test11_EmptyPortfolioHandling() {
        Portfolio empty = new Portfolio();
        double total = ReturnCalculator.calculateCurrentPortfolioValue(empty.getAllInvestments());
        double ret = ReturnCalculator.calculateOverallReturnPercentage(empty.getAllInvestments());
        assertCondition("Test 11: Empty Portfolio Safe Handling (0.0 value & 0.0% return)", total == 0.0 && ret == 0.0);
    }

    private static void test12_InvalidInputHandling() {
        boolean caughtNegativeQty = false;
        try {
            new Stock("STK-9", "Bad Stock", -5, 100, 120, "2025-01-01", "BAD");
        } catch (IllegalArgumentException e) {
            caughtNegativeQty = true;
        }
        assertCondition("Test 12: Reject Negative Quantity with IllegalArgumentException", caughtNegativeQty);
    }

    private static void test13_CsvLoadAndSave() {
        DataManager.ensureSampleDataExists();
        Map<String, Set<String>> holdings = DataManager.loadFundHoldings();
        List<Investment> loaded = DataManager.loadInvestments(holdings);
        assertCondition("Test 13: CSV Persistence Load & Save", !loaded.isEmpty() && !holdings.isEmpty());
    }

    private static void test14_UserCsvImportValidation() {
        try {
            File tempTemplate = new File("data/test_template.csv");
            DataManager.writeSampleImportTemplate(tempTemplate);
            Portfolio p = new Portfolio();
            DataManager.CsvValidationReport report = DataManager.validateImportCsv(tempTemplate, p);
            tempTemplate.delete();
            assertCondition("Test 14: User CSV Import Header & Row Validation", report.isHeaderValid() && report.getValidRowCount() == 3);
        } catch (Exception e) {
            assertCondition("Test 14: User CSV Import Header & Row Validation", false);
        }
    }
}
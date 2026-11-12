import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects overlapping stock holdings between two mutual funds using HashSet intersection (Module D).
 */
public class OverlapDetector {

    public static class OverlapResult {
        private final String fundAName;
        private final String fundBName;
        private final List<String> fundAHoldings;
        private final List<String> fundBHoldings;
        private final List<String> commonHoldings;
        private final double overlapRelativeToA;
        private final double overlapRelativeToB;

        public OverlapResult(String fundAName, String fundBName,
                             List<String> fundAHoldings, List<String> fundBHoldings,
                             List<String> commonHoldings, double overlapRelativeToA, double overlapRelativeToB) {
            this.fundAName = fundAName;
            this.fundBName = fundBName;
            this.fundAHoldings = fundAHoldings;
            this.fundBHoldings = fundBHoldings;
            this.commonHoldings = commonHoldings;
            this.overlapRelativeToA = overlapRelativeToA;
            this.overlapRelativeToB = overlapRelativeToB;
        }

        public String getFundAName() { return fundAName; }
        public String getFundBName() { return fundBName; }
        public List<String> getFundAHoldings() { return fundAHoldings; }
        public List<String> getFundBHoldings() { return fundBHoldings; }
        public List<String> getCommonHoldings() { return commonHoldings; }
        public int getCommonCount() { return commonHoldings.size(); }
        public double getOverlapRelativeToA() { return overlapRelativeToA; }
        public double getOverlapRelativeToB() { return overlapRelativeToB; }
    }

    /**
     * Compares two mutual funds and finds common stocks using HashSet retainAll().
     */
    public static OverlapResult analyzeOverlap( String fundAName, Set<String> holdingsA,
                                                String fundBName, Set<String> holdingsB) {
        Set<String> normalizedA = new HashSet<>();
        Set<String> normalizedB = new HashSet<>();

        if (holdingsA != null) {
            for (String item : holdingsA) {
                if (item != null && !item.trim().isEmpty()) {
                    normalizedA.add(item.trim());
                }
            }
        }
        if (holdingsB != null) {
            for (String item : holdingsB) {
                if (item != null && !item.trim().isEmpty()) {
                    normalizedB.add(item.trim());
                }
            }
        }

        // Perform Set Intersection using HashSet
        Set<String> intersection = new HashSet<>(normalizedA);
        intersection.retainAll(normalizedB);

        List<String> sortedA = new ArrayList<>(normalizedA);
        List<String> sortedB = new ArrayList<>(normalizedB);
        List<String> sortedCommon = new ArrayList<>(intersection);
        Collections.sort(sortedA);
        Collections.sort(sortedB);
        Collections.sort(sortedCommon);

        double pctA = normalizedA.isEmpty() ? 0.0 : ((double) sortedCommon.size() / normalizedA.size()) * 100.0;
        double pctB = normalizedB.isEmpty() ? 0.0 : ((double) sortedCommon.size() / normalizedB.size()) * 100.0;

        return new OverlapResult(fundAName, fundBName, sortedA, sortedB, sortedCommon, pctA, pctB);
    }
}
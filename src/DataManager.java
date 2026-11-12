import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles reading, writing, and validating CSV files for investments, fund holdings, and historical prices.
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String INVESTMENTS_FILE = DATA_DIR + File.separator + "investments.csv";
    private static final String HOLDINGS_FILE = DATA_DIR + File.separator + "fund_holdings.csv";
    private static final String PRICES_FILE = DATA_DIR + File.separator + "historical_prices.csv";

    public static class CsvImportRow {
        private final int rowNumber;
        private final String id;
        private final String name;
        private final String type;
        private final double quantity;
        private final double purchasePrice;
        private final double currentPrice;
        private final String purchaseDate;
        private final String extraDetail;
        private final String status;
        private final boolean validForImport;

        public CsvImportRow(int rowNumber, String id, String name, String type, double quantity,
                            double purchasePrice, double currentPrice, String purchaseDate,
                            String extraDetail, String status, boolean validForImport) {
            this.rowNumber = rowNumber;
            this.id = id;
            this.name = name;
            this.type = type;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
            this.currentPrice = currentPrice;
            this.purchaseDate = purchaseDate;
            this.extraDetail = extraDetail;
            this.status = status;
            this.validForImport = validForImport;
        }

        public int getRowNumber() { return rowNumber; }
        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public double getQuantity() { return quantity; }
        public double getPurchasePrice() { return purchasePrice; }
        public double getCurrentPrice() { return currentPrice; }
        public String getPurchaseDate() { return purchaseDate; }
        public String getExtraDetail() { return extraDetail; }
        public String getStatus() { return status; }
        public boolean isValidForImport() { return validForImport; }
    }

    public static class CsvValidationReport {
        private final boolean headerValid;
        private final String headerErrorMessage;
        private final List<CsvImportRow> rows;

        public CsvValidationReport(boolean headerValid, String headerErrorMessage, List<CsvImportRow> rows) {
            this.headerValid = headerValid;
            this.headerErrorMessage = headerErrorMessage;
            this.rows = rows;
        }

        public boolean isHeaderValid() { return headerValid; }
        public String getHeaderErrorMessage() { return headerErrorMessage; }
        public List<CsvImportRow> getRows() { return rows; }

        public int getValidRowCount() {
            int count = 0;
            for (CsvImportRow r : rows) {
                if (r.isValidForImport()) count++;
            }
            return count;
        }
    }

    public static void ensureSampleDataExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File invFile = new File(INVESTMENTS_FILE);
        if (!invFile.exists()) {
            writeDefaultInvestments(invFile);
        }

        File holdFile = new File(HOLDINGS_FILE);
        if (!holdFile.exists()) {
            writeDefaultHoldings(holdFile);
        }

        File priceFile = new File(PRICES_FILE);
        if (!priceFile.exists()) {
            writeDefaultHistoricalPrices(priceFile);
        }
    }

    private static void writeDefaultInvestments(File file) {
        String[] lines = {
            "ID,Name,Type,Quantity,PurchasePrice,CurrentPrice,PurchaseDate,TickerOrCategory",
            "STK-101,Reliance Industries,Stock,18,2450.00,2945.00,2024-01-15,RELIANCE",
            "STK-102,Tata Consultancy Services,Stock,12,3580.00,3920.00,2024-02-10,TCS",
            "STK-103,Infosys,Stock,25,1480.00,1415.00,2024-03-05,INFY",
            "STK-104,HDFC Bank,Stock,30,1520.00,1645.00,2024-03-18,HDFCBANK",
            "MF-101,Axis Bluechip Fund,Mutual Fund,450,48.50,56.20,2024-01-20,Large Cap Equity",
            "MF-102,Mirae Asset Large Cap Fund,Mutual Fund,320,84.00,96.40,2024-02-14,Large Cap Equity",
            "MF-103,Parag Parikh Flexi Cap Fund,Mutual Fund,280,62.00,74.80,2024-04-02,Flexi Cap Equity"
        };
        writeLines(file, lines);
    }

    private static void writeDefaultHoldings(File file) {
        String[] lines = {
            "FundName,Holdings",
            "Axis Bluechip Fund,Reliance Industries;Tata Consultancy Services;Infosys;HDFC Bank;ICICI Bank;Bajaj Finance;Avenue Supermarts;Larsen & Toubro",
            "Mirae Asset Large Cap Fund,Reliance Industries;Tata Consultancy Services;Infosys;HDFC Bank;ICICI Bank;State Bank of India;Bharti Airtel;Axis Bank",
            "Parag Parikh Flexi Cap Fund,HDFC Bank;ICICI Bank;Bajaj Holdings;ITC;Coal India;Maruti Suzuki;HCL Technologies;Axis Bank",
            "SBI Contra Fund,State Bank of India;Reliance Industries;ITC;NTPC;Power Grid;Coal India;Oil & Natural Gas Corp;Tata Steel"
        };
        writeLines(file, lines);
    }

    private static void writeDefaultHistoricalPrices(File file) {
        String[] lines = {
            "Date,Reliance Industries,Tata Consultancy Services,Infosys,HDFC Bank",
            "2025-01-01,2850.00,3780.00,1450.00,1580.00",
            "2025-01-02,2868.50,3805.00,1462.00,1574.00",
            "2025-01-03,2845.00,3790.00,1455.00,1585.00",
            "2025-01-06,2872.00,3825.00,1470.00,1592.00",
            "2025-01-07,2890.00,3840.00,1478.00,1588.00",
            "2025-01-08,2875.00,3812.00,1460.00,1595.00",
            "2025-01-09,2898.00,3850.00,1474.00,1602.00",
            "2025-01-10,2912.00,3878.00,1485.00,1596.00",
            "2025-01-13,2888.00,3845.00,1468.00,1608.00",
            "2025-01-14,2905.00,3862.00,1472.00,1615.00",
            "2025-01-15,2924.00,3890.00,1481.00,1610.00",
            "2025-01-16,2910.00,3872.00,1465.00,1618.00",
            "2025-01-17,2935.00,3904.00,1470.00,1625.00",
            "2025-01-20,2918.00,3885.00,1452.00,1619.00",
            "2025-01-21,2940.00,3910.00,1458.00,1630.00",
            "2025-01-22,2955.00,3932.00,1464.00,1624.00",
            "2025-01-23,2932.00,3900.00,1445.00,1632.00",
            "2025-01-24,2948.00,3918.00,1448.00,1638.00",
            "2025-01-27,2925.00,3895.00,1430.00,1629.00",
            "2025-01-28,2950.00,3915.00,1436.00,1640.00",
            "2025-01-29,2962.00,3935.00,1440.00,1635.00",
            "2025-01-30,2938.00,3908.00,1422.00,1642.00",
            "2025-01-31,2945.00,3920.00,1415.00,1645.00"
        };
        writeLines(file, lines);
    }

    private static void writeLines(File file, String[] lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing default data file " + file.getName() + ": " + e.getMessage());
        }
    }

    public static List<Investment> loadInvestments(Map<String, Set<String>> fundHoldingsMap) {
        ensureSampleDataExists();
        List<Investment> list = new ArrayList<>();
        File file = new File(INVESTMENTS_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;

                String id = parts[0].trim();
                String name = parts[1].trim();
                String type = parts[2].trim();
                double qty = Double.parseDouble(parts[3].trim());
                double buyPrice = Double.parseDouble(parts[4].trim());
                double curPrice = Double.parseDouble(parts[5].trim());
                String date = parts[6].trim();
                String extra = parts.length >= 8 ? parts[7].trim() : "";

                if (type.equalsIgnoreCase("Stock")) {
                    list.add(new Stock(id, name, qty, buyPrice, curPrice, date, extra));
                } else if (type.equalsIgnoreCase("Mutual Fund")) {
                    MutualFund mf = new MutualFund(id, name, qty, buyPrice, curPrice, date, extra);
                    if (fundHoldingsMap != null && fundHoldingsMap.containsKey(name)) {
                        mf.setHoldings(fundHoldingsMap.get(name));
                    }
                    list.add(mf);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading investments: " + e.getMessage());
        }
        return list;
    }

    public static void saveInvestments(List<Investment> investments) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVESTMENTS_FILE))) {
            writer.write("ID,Name,Type,Quantity,PurchasePrice,CurrentPrice,PurchaseDate,TickerOrCategory");
            writer.newLine();
            for (Investment inv : investments) {
                String extra = "";
                if (inv instanceof Stock) {
                    extra = ((Stock) inv).getTickerSymbol();
                } else if (inv instanceof MutualFund) {
                    extra = ((MutualFund) inv).getFundCategory();
                }
                String row = String.format("%s,%s,%s,%.4f,%.2f,%.2f,%s,%s",
                        sanitizeCsv(inv.getId()),
                        sanitizeCsv(inv.getName()),
                        sanitizeCsv(inv.getInvestmentType()),
                        inv.getQuantity(),
                        inv.getPurchasePrice(),
                        inv.getCurrentPrice(),
                        sanitizeCsv(inv.getPurchaseDate()),
                        sanitizeCsv(extra));
                writer.write(row);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save investments to CSV: " + e.getMessage());
        }
    }

    public static Map<String, Set<String>> loadFundHoldings() {
        ensureSampleDataExists();
        Map<String, Set<String>> map = new LinkedHashMap<>();
        File file = new File(HOLDINGS_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                String fundName = parts[0].trim();
                String[] stocks = parts[1].split(";");
                Set<String> holdings = new LinkedHashSet<>();
                for (String s : stocks) {
                    if (!s.trim().isEmpty()) {
                        holdings.add(s.trim());
                    }
                }
                map.put(fundName, holdings);
            }
        } catch (IOException e) {
            System.err.println("Error loading fund holdings: " + e.getMessage());
        }
        return map;
    }

    public static void saveFundHoldings(Map<String, Set<String>> holdingsMap) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HOLDINGS_FILE))) {
            writer.write("FundName,Holdings");
            writer.newLine();
            for (Map.Entry<String, Set<String>> entry : holdingsMap.entrySet()) {
                String joined = String.join(";", entry.getValue());
                writer.write(sanitizeCsv(entry.getKey()) + "," + joined);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fund holdings: " + e.getMessage());
        }
    }

    public static Map<String, List<RiskAnalyzer.PricePoint>> loadHistoricalPrices() {
        ensureSampleDataExists();
        Map<String, List<RiskAnalyzer.PricePoint>> priceData = new LinkedHashMap<>();
        File file = new File(PRICES_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return priceData;

            String[] headers = headerLine.split(",");
            for (int i = 1; i < headers.length; i++) {
                priceData.put(headers[i].trim(), new ArrayList<>());
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String date = parts[0].trim();
                for (int i = 1; i < parts.length && i < headers.length; i++) {
                    String stockName = headers[i].trim();
                    String valStr = parts[i].trim();
                    if (!valStr.isEmpty()) {
                        double price = Double.parseDouble(valStr);
                        priceData.get(stockName).add(new RiskAnalyzer.PricePoint(date, price));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading historical prices: " + e.getMessage());
        }
        return priceData;
    }

    /**
     * Validates an external user CSV file before importing into the portfolio.
     * Required columns: ID, Name, Type, Quantity, PurchasePrice, CurrentPrice, PurchaseDate
     * Optional column: TickerOrCategory
     */
    public static CsvValidationReport validateImportCsv(File csvFile, Portfolio currentPortfolio) {
        List<CsvImportRow> rows = new ArrayList<>();
        if (csvFile == null || !csvFile.exists()) {
            return new CsvValidationReport(false, "Selected file does not exist.", rows);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                return new CsvValidationReport(false, "The CSV file is empty.", rows);
            }

            String[] headers = headerLine.split(",");
            List<String> requiredHeaders = Arrays.asList("ID", "Name", "Type", "Quantity", "PurchasePrice", "CurrentPrice", "PurchaseDate");
            if (headers.length < requiredHeaders.size()) {
                return new CsvValidationReport(false,
                        "Missing required columns. Expected header: ID,Name,Type,Quantity,PurchasePrice,CurrentPrice,PurchaseDate,TickerOrCategory",
                        rows);
            }

            for (int i = 0; i < requiredHeaders.size(); i++) {
                String actual = headers[i].trim().replaceAll("[^a-zA-Z]", "");
                String expected = requiredHeaders.get(i);
                if (!actual.equalsIgnoreCase(expected)) {
                    return new CsvValidationReport(false,
                            "Invalid column header at position " + (i + 1) + ": found '" + headers[i].trim() + "', expected '" + expected + "'.",
                            rows);
                }
            }

            Set<String> seenIdsInFile = new LinkedHashSet<>();
            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length < 7) {
                    rows.add(new CsvImportRow(rowNum, "-", "-", "-", 0, 0, 0, "-", "",
                            "Error: Row has fewer than 7 columns", false));
                    continue;
                }

                String id = cols[0].trim();
                String name = cols[1].trim();
                String type = cols[2].trim();
                String qtyStr = cols[3].trim();
                String buyStr = cols[4].trim();
                String curStr = cols[5].trim();
                String date = cols[6].trim();
                String extra = cols.length >= 8 ? cols[7].trim() : "";

                double qty = 0, buyPrice = 0, curPrice = 0;
                List<String> errors = new ArrayList<>();

                if (id.isEmpty()) errors.add("Empty ID");
                if (name.isEmpty()) errors.add("Empty Name");
                if (!type.equalsIgnoreCase("Stock") && !type.equalsIgnoreCase("Mutual Fund")) {
                    errors.add("Type must be 'Stock' or 'Mutual Fund'");
                }
                if (date.isEmpty()) errors.add("Empty Date");

                try {
                    qty = Double.parseDouble(qtyStr);
                    if (qty <= 0) errors.add("Quantity must be > 0");
                } catch (NumberFormatException e) {
                    errors.add("Invalid Quantity number");
                }

                try {
                    buyPrice = Double.parseDouble(buyStr);
                    if (buyPrice <= 0) errors.add("PurchasePrice must be > 0");
                } catch (NumberFormatException e) {
                    errors.add("Invalid PurchasePrice number");
                }

                try {
                    curPrice = Double.parseDouble(curStr);
                    if (curPrice < 0) errors.add("CurrentPrice cannot be negative");
                } catch (NumberFormatException e) {
                    errors.add("Invalid CurrentPrice number");
                }

                if (errors.isEmpty()) {
                    if (seenIdsInFile.contains(id.toUpperCase())) {
                        errors.add("Duplicate ID '" + id + "' within CSV file");
                    } else if (currentPortfolio.findById(id) != null) {
                        errors.add("Duplicate ID '" + id + "' already in portfolio");
                    } else if (currentPortfolio.isDuplicateRecord(name, type, date, buyPrice)) {
                        errors.add("Duplicate investment record already in portfolio");
                    }
                }

                if (!id.isEmpty()) {
                    seenIdsInFile.add(id.toUpperCase());
                }

                if (errors.isEmpty()) {
                    String normalizedType = type.equalsIgnoreCase("Stock") ? "Stock" : "Mutual Fund";
                    rows.add(new CsvImportRow(rowNum, id, name, normalizedType, qty, buyPrice, curPrice, date, extra,
                            "Ready to Import", true));
                } else {
                    rows.add(new CsvImportRow(rowNum, id, name, type, qty, buyPrice, curPrice, date, extra,
                            "Skipped: " + String.join("; ", errors), false));
                }
            }

            if (rows.isEmpty()) {
                return new CsvValidationReport(false, "The CSV file contains headers but no data rows.", rows);
            }

            return new CsvValidationReport(true, "CSV file parsed successfully.", rows);

        } catch (IOException e) {
            return new CsvValidationReport(false, "Error reading file: " + e.getMessage(), rows);
        }
    }

    public static void writeSampleImportTemplate(File destination) throws IOException {
        String[] templateLines = {
            "ID,Name,Type,Quantity,PurchasePrice,CurrentPrice,PurchaseDate,TickerOrCategory",
            "STK-201,Larsen & Toubro,Stock,10,3350.00,3620.00,2024-05-12,LT",
            "STK-202,ITC Limited,Stock,50,425.00,468.50,2024-06-01,ITC",
            "MF-201,SBI Contra Fund,Mutual Fund,200,310.00,348.00,2024-05-18,Contra Equity"
        };
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination))) {
            for (String line : templateLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static String sanitizeCsv(String input) {
        if (input == null) return "";
        return input.replace(",", " ").trim();
    }
}
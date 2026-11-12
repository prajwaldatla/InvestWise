import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Main Swing user interface for InvestWise.
 * Implements a clean, minimalist desktop fintech design with custom Graphics2D charts.
 */
public class Dashboard extends JFrame {

    // Color Palette (Section 4 Design Specification)
    private static final Color BG_MAIN = new Color(0xF4, 0xF6, 0xF8);
    private static final Color BG_SIDEBAR = new Color(0x17, 0x2B, 0x4D);
    private static final Color BG_SIDEBAR_ACTIVE = new Color(0x24, 0x3B, 0x64);
    private static final Color ACCENT_BLUE = new Color(0x34, 0x78, 0xC7);
    private static final Color POSITIVE_GREEN = new Color(0x23, 0x86, 0x5B);
    private static final Color NEGATIVE_RED = new Color(0xC6, 0x53, 0x53);
    private static final Color TEXT_PRIMARY = new Color(0x26, 0x34, 0x45);
    private static final Color TEXT_SECONDARY = new Color(0x7B, 0x87, 0x94);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE3, 0xE8, 0xEE);

    // Typography
    private static final String FONT_FAMILY = getPreferredFontFamily();
    private static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    private static final Font FONT_SECTION = new Font(FONT_FAMILY, Font.BOLD, 15);
    private static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    private static final Font FONT_METRIC = new Font(FONT_FAMILY, Font.BOLD, 20);

    // Application State & Models
    private final Portfolio portfolio;
    private Map<String, Set<String>> fundHoldingsMap;
    private Map<String, List<RiskAnalyzer.PricePoint>> historicalPricesMap;

    // Navigation & Layout
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();

    // Dashboard Components
    private JLabel lblDashInvested;
    private JLabel lblDashCurrent;
    private JLabel lblDashProfitLoss;
    private JLabel lblDashReturnPct;
    private DefaultTableModel dashTableModel;
    private DonutChartPanel dashDonutChart;

    // Portfolio Management Components
    private JTextField txtSearch;
    private JComboBox<String> cmbTypeFilter;
    private JTable portfolioTable;
    private DefaultTableModel portfolioTableModel;

    // Asset Allocation Components
    private DonutChartPanel allocationDonutChart;
    private DefaultTableModel allocationClassTableModel;
    private DefaultTableModel allocationHoldingTableModel;
    private JLabel lblAllocTotalValue;

    // Mutual Fund Overlap Components
    private JComboBox<String> cmbFundA;
    private JComboBox<String> cmbFundB;
    private JLabel lblOverlapSummary;
    private JLabel lblOverlapPctA;
    private JLabel lblOverlapPctB;
    private DefaultListModel<String> listModelFundA;
    private DefaultListModel<String> listModelFundB;
    private DefaultListModel<String> listModelCommon;

    // Risk & Correlation Components
    private JComboBox<String> cmbRiskStock;
    private JLabel lblMeanDailyReturn;
    private JLabel lblSampleVariance;
    private JLabel lblStdDeviation;
    private DefaultTableModel riskTableModel;
    private ReturnBarChartPanel riskBarChart;

    private JComboBox<String> cmbCorrStockA;
    private JComboBox<String> cmbCorrStockB;
    private JLabel lblCorrValue;
    private JTextArea txtCorrInterpretation;

    public Dashboard() {
        super("InvestWise — Investment Portfolio Risk & Diversification Analyzer");
        this.fundHoldingsMap = DataManager.loadFundHoldings();
        this.historicalPricesMap = DataManager.loadHistoricalPrices();
        this.portfolio = new Portfolio();
        this.portfolio.setAllInvestments(DataManager.loadInvestments(fundHoldingsMap));

        configureWindow();
        initLayout();
        refreshAllViews();
    }

    private static String getPreferredFontFamily() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Set<String> available = new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames()));
        if (available.contains("Inter")) return "Inter";
        if (available.contains("Segoe UI")) return "Segoe UI";
        if (available.contains("Helvetica Neue")) return "Helvetica Neue";
        return "SansSerif";
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 780);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout());
    }

    private void initLayout() {
        add(createSidebar(), BorderLayout.WEST);

        contentCards.setBackground(BG_MAIN);
        contentCards.add(createDashboardPanel(), "DASHBOARD");
        contentCards.add(createPortfolioPanel(), "PORTFOLIO");
        contentCards.add(createAssetAllocationPanel(), "ALLOCATION");
        contentCards.add(createOverlapPanel(), "OVERLAP");
        contentCards.add(createRiskAndCorrelationPanel(), "RISK");
        contentCards.add(createAboutPanel(), "ABOUT");

        add(contentCards, BorderLayout.CENTER);
        switchCard("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

        JLabel brandTitle = new JLabel("InvestWise");
        brandTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("Portfolio & Risk Analyzer");
        brandSub.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        brandSub.setForeground(new Color(0xA6, 0xB8, 0xD4));
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brandTitle);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(brandSub);
        sidebar.add(Box.createVerticalStrut(28));

        addNavButton(sidebar, "Dashboard", "DASHBOARD");
        addNavButton(sidebar, "My Portfolio", "PORTFOLIO");
        addNavButton(sidebar, "Asset Allocation", "ALLOCATION");
        addNavButton(sidebar, "Fund Overlap", "OVERLAP");
        addNavButton(sidebar, "Risk & Correlation", "RISK");
        addNavButton(sidebar, "Settings & About", "ABOUT");

        sidebar.add(Box.createVerticalGlue());

        JLabel footerNote = new JLabel("<html><span style='color:#8FA4C4;font-size:10px;'>CAM Undergraduate OOPS Lab<br>Analytical Educational Tool</span></html>");
        footerNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footerNote);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String text, String cardKey) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setPreferredSize(new Dimension(210, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> switchCard(cardKey));
        navButtons.put(cardKey, btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(6));
    }

    private void switchCard(String cardKey) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(cardKey);
            JButton btn = entry.getValue();
            btn.setBackground(active ? BG_SIDEBAR_ACTIVE : BG_SIDEBAR);
            btn.setFont(new Font(FONT_FAMILY, active ? Font.BOLD : Font.PLAIN, 14));
        }
        refreshAllViews();
        cardLayout.show(contentCards, cardKey);
    }

    // =========================================================================
    // 1. DASHBOARD VIEW
    // =========================================================================
    private JPanel createDashboardPanel() {
        JPanel page = createPageContainer("Portfolio Overview",
                "Summary of total capital invested, current valuation, profit/loss performance, and asset distribution.");

        // 4 Summary Metric Strip
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);

        lblDashInvested = createMetricValueLabel();
        lblDashCurrent = createMetricValueLabel();
        lblDashProfitLoss = createMetricValueLabel();
        lblDashReturnPct = createMetricValueLabel();

        metricsGrid.add(createMetricCard("Total Amount Invested", lblDashInvested));
        metricsGrid.add(createMetricCard("Current Portfolio Value", lblDashCurrent));
        metricsGrid.add(createMetricCard("Total Profit / Loss", lblDashProfitLoss));
        metricsGrid.add(createMetricCard("Overall Return (%)", lblDashReturnPct));

        // Center Split: Table on Left, Donut Chart on Right
        JPanel centerSplit = new JPanel(new BorderLayout(16, 0));
        centerSplit.setOpaque(false);

        JPanel tableCard = createSectionPanel("Individual Investment Performance");
        String[] cols = {"ID", "Investment Name", "Type", "Invested", "Current Value", "Profit / Loss", "Return %"};
        dashTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable dashTable = createStyledTable(dashTableModel, 5, 6);
        tableCard.add(new JScrollPane(dashTable), BorderLayout.CENTER);

        JPanel chartCard = createSectionPanel("Asset Class Breakdown");
        chartCard.setPreferredSize(new Dimension(340, 0));
        dashDonutChart = new DonutChartPanel();
        chartCard.add(dashDonutChart, BorderLayout.CENTER);

        centerSplit.add(tableCard, BorderLayout.CENTER);
        centerSplit.add(chartCard, BorderLayout.EAST);

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        body.add(metricsGrid, BorderLayout.NORTH);
        body.add(centerSplit, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // =========================================================================
    // 2. MY PORTFOLIO VIEW (MODULE A & B + CSV IMPORT)
    // =========================================================================
    private JPanel createPortfolioPanel() {
        JPanel page = createPageContainer("My Portfolio",
                "Add, edit, delete, search, or import stock and mutual fund investments saved locally in CSV format.");

        JPanel topControls = new JPanel(new BorderLayout(12, 0));
        topControls.setOpaque(false);

        // Search & Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(createLabel("Search:"));
        txtSearch = new JTextField(16);
        txtSearch.setFont(FONT_BODY);
        txtSearch.addCaretListener(e -> refreshPortfolioTable());
        filterPanel.add(txtSearch);

        filterPanel.add(createLabel("Type:"));
        cmbTypeFilter = new JComboBox<>(new String[]{"All", "Stock", "Mutual Fund"});
        cmbTypeFilter.setFont(FONT_BODY);
        cmbTypeFilter.addActionListener(e -> refreshPortfolioTable());
        filterPanel.add(cmbTypeFilter);

        // Action Buttons (Add, Edit, Delete, Import CSV, Download Sample CSV)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JButton btnAddStock = createPrimaryButton("+ Add Stock");
        JButton btnAddFund = createPrimaryButton("+ Add Mutual Fund");
        JButton btnEdit = createSecondaryButton("Edit Selected");
        JButton btnDelete = createDangerButton("Delete");
        JButton btnImportCsv = createSecondaryButton("Import CSV...");
        JButton btnTemplate = createSecondaryButton("Sample CSV Template");

        btnAddStock.addActionListener(e -> showAddOrEditDialog("Stock", null));
        btnAddFund.addActionListener(e -> showAddOrEditDialog("Mutual Fund", null));
        btnEdit.addActionListener(e -> handleEditSelectedInvestment());
        btnDelete.addActionListener(e -> handleDeleteSelectedInvestment());
        btnImportCsv.addActionListener(e -> handleImportCsvWorkflow());
        btnTemplate.addActionListener(e -> handleDownloadTemplateCsv());

        actionPanel.add(btnAddStock);
        actionPanel.add(btnAddFund);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnImportCsv);
        actionPanel.add(btnTemplate);

        topControls.add(filterPanel, BorderLayout.WEST);
        topControls.add(actionPanel, BorderLayout.EAST);

        // Portfolio Table
        JPanel tableContainer = createSectionPanel("Recorded Holdings");
        String[] cols = {
            "ID", "Name", "Type", "Details", "Qty / Units",
            "Buy Price", "Current Price", "Buy Date",
            "Initial Invested", "Current Value", "Profit / Loss", "Return %"
        };
        portfolioTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        portfolioTable = createStyledTable(portfolioTableModel, 10, 11);
        tableContainer.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);

        JPanel mainBody = new JPanel(new BorderLayout(0, 14));
        mainBody.setOpaque(false);
        mainBody.add(topControls, BorderLayout.NORTH);
        mainBody.add(tableContainer, BorderLayout.CENTER);

        page.add(mainBody, BorderLayout.CENTER);
        return page;
    }

    // =========================================================================
    // 3. ASSET ALLOCATION VIEW (MODULE C)
    // =========================================================================
    private JPanel createAssetAllocationPanel() {
        JPanel page = createPageContainer("Asset Allocation Analysis",
                "Distribution of your portfolio across asset classes and individual investments based on current market value.");

        JPanel split = new JPanel(new GridLayout(1, 2, 18, 0));
        split.setOpaque(false);

        JPanel leftChartCard = createSectionPanel("Portfolio Allocation Chart");
        allocationDonutChart = new DonutChartPanel();
        lblAllocTotalValue = new JLabel("Total Portfolio Market Value: ₹0.00", SwingConstants.CENTER);
        lblAllocTotalValue.setFont(FONT_SECTION);
        lblAllocTotalValue.setForeground(TEXT_PRIMARY);
        lblAllocTotalValue.setBorder(new EmptyBorder(10, 0, 4, 0));
        leftChartCard.add(allocationDonutChart, BorderLayout.CENTER);
        leftChartCard.add(lblAllocTotalValue, BorderLayout.SOUTH);

        JPanel rightTables = new JPanel(new GridLayout(2, 1, 0, 16));
        rightTables.setOpaque(false);

        JPanel classCard = createSectionPanel("Allocation by Asset Type");
        allocationClassTableModel = new DefaultTableModel(new String[]{"Asset Class", "Current Market Value", "Weight (%)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable classTable = createStyledTable(allocationClassTableModel, -1, -1);
        classCard.add(new JScrollPane(classTable), BorderLayout.CENTER);

        JPanel holdingCard = createSectionPanel("Allocation by Individual Holding");
        allocationHoldingTableModel = new DefaultTableModel(new String[]{"Investment Name", "Type", "Current Value", "Portfolio Share (%)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable holdingTable = createStyledTable(allocationHoldingTableModel, -1, -1);
        holdingCard.add(new JScrollPane(holdingTable), BorderLayout.CENTER);

        rightTables.add(classCard);
        rightTables.add(holdingCard);

        split.add(leftChartCard);
        split.add(rightTables);

        page.add(split, BorderLayout.CENTER);
        return page;
    }

    // =========================================================================
    // 4. MUTUAL FUND OVERLAP VIEW (MODULE D)
    // =========================================================================
    private JPanel createOverlapPanel() {
        JPanel page = createPageContainer("Mutual Fund Overlap Detection",
                "Identify common underlying stock holdings between two mutual funds using Java HashSet intersection.");

        JPanel topBar = createSectionPanel("Select Mutual Funds to Compare");
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        controls.setOpaque(false);

        cmbFundA = new JComboBox<>();
        cmbFundB = new JComboBox<>();
        cmbFundA.setFont(FONT_BODY);
        cmbFundB.setFont(FONT_BODY);

        cmbFundA.addActionListener(e -> refreshOverlapComparison());
        cmbFundB.addActionListener(e -> refreshOverlapComparison());

        JButton btnEditHoldings = createSecondaryButton("Manage Fund Holdings...");
        btnEditHoldings.addActionListener(e -> showManageHoldingsDialog());

        controls.add(createLabel("Fund A:"));
        controls.add(cmbFundA);
        controls.add(createLabel("Fund B:"));
        controls.add(cmbFundB);
        controls.add(btnEditHoldings);
        topBar.add(controls, BorderLayout.CENTER);

        // Summary Strip
        JPanel summaryGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        summaryGrid.setOpaque(false);
        lblOverlapSummary = createMetricValueLabel();
        lblOverlapPctA = createMetricValueLabel();
        lblOverlapPctB = createMetricValueLabel();

        summaryGrid.add(createMetricCard("Overlapping Stocks Count", lblOverlapSummary));
        summaryGrid.add(createMetricCard("Overlap Relative to Fund A", lblOverlapPctA));
        summaryGrid.add(createMetricCard("Overlap Relative to Fund B", lblOverlapPctB));

        // 3 Columns for Lists
        JPanel listsGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        listsGrid.setOpaque(false);

        listModelFundA = new DefaultListModel<>();
        listModelCommon = new DefaultListModel<>();
        listModelFundB = new DefaultListModel<>();

        listsGrid.add(createListCard("Fund A Holdings", listModelFundA, TEXT_PRIMARY));
        listsGrid.add(createListCard("Common Holdings (Intersection)", listModelCommon, ACCENT_BLUE));
        listsGrid.add(createListCard("Fund B Holdings", listModelFundB, TEXT_PRIMARY));

        JLabel disclaimer = new JLabel(
                "Note: This is a count-based unweighted overlap calculation (|A ∩ B| / |A|), not a weighted portfolio overlap calculation.");
        disclaimer.setFont(FONT_SUBTITLE);
        disclaimer.setForeground(TEXT_SECONDARY);
        disclaimer.setBorder(new EmptyBorder(6, 2, 0, 2));

        JPanel centerBody = new JPanel(new BorderLayout(0, 14));
        centerBody.setOpaque(false);
        centerBody.add(summaryGrid, BorderLayout.NORTH);
        centerBody.add(listsGrid, BorderLayout.CENTER);
        centerBody.add(disclaimer, BorderLayout.SOUTH);

        JPanel fullBody = new JPanel(new BorderLayout(0, 14));
        fullBody.setOpaque(false);
        fullBody.add(topBar, BorderLayout.NORTH);
        fullBody.add(centerBody, BorderLayout.CENTER);

        page.add(fullBody, BorderLayout.CENTER);
        return page;
    }

    // =========================================================================
    // 5. RISK & CORRELATION VIEW (MODULE E & MODULE F)
    // =========================================================================
    private JPanel createRiskAndCorrelationPanel() {
        JPanel page = createPageContainer("Historical Risk & Correlation Analysis",
                "Statistical volatility metrics (mean, sample variance, standard deviation) and Pearson correlation of daily stock returns.");

        JPanel split = new JPanel(new GridLayout(1, 2, 18, 0));
        split.setOpaque(false);

        // LEFT: Module E - Risk Analysis
        JPanel riskCard = createSectionPanel("Module E: Single-Stock Historical Volatility");
        JPanel riskTop = new JPanel(new BorderLayout(0, 10));
        riskTop.setOpaque(false);

        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectorRow.setOpaque(false);
        selectorRow.add(createLabel("Select Stock:"));
        cmbRiskStock = new JComboBox<>();
        cmbRiskStock.setFont(FONT_BODY);
        cmbRiskStock.addActionListener(e -> refreshRiskAnalysis());
        selectorRow.add(cmbRiskStock);

        JPanel statsStrip = new JPanel(new GridLayout(1, 3, 8, 0));
        statsStrip.setOpaque(false);
        lblMeanDailyReturn = new JLabel("0.00%");
        lblSampleVariance = new JLabel("0.0000");
        lblStdDeviation = new JLabel("0.00%");
        statsStrip.add(createMiniStatBox("Avg Daily Return", lblMeanDailyReturn));
        statsStrip.add(createMiniStatBox("Sample Variance (s²)", lblSampleVariance));
        statsStrip.add(createMiniStatBox("Daily Volatility (s)", lblStdDeviation));

        riskTop.add(selectorRow, BorderLayout.NORTH);
        riskTop.add(statsStrip, BorderLayout.CENTER);

        riskTableModel = new DefaultTableModel(new String[]{"Trading Date", "Close Price (₹)", "Daily Return (%)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable riskTable = createStyledTable(riskTableModel, 2, 2);

        riskBarChart = new ReturnBarChartPanel();
        riskBarChart.setPreferredSize(new Dimension(0, 150));

        JLabel riskNote = new JLabel("<html><i>Sample variance divides by (n - 1). Standard deviation measures historical volatility, not guaranteed future risk.</i></html>");
        riskNote.setFont(new Font(FONT_FAMILY, Font.PLAIN, 11));
        riskNote.setForeground(TEXT_SECONDARY);

        JPanel riskBottom = new JPanel(new BorderLayout(0, 6));
        riskBottom.setOpaque(false);
        riskBottom.add(riskBarChart, BorderLayout.CENTER);
        riskBottom.add(riskNote, BorderLayout.SOUTH);

        JPanel riskContent = new JPanel(new BorderLayout(0, 10));
        riskContent.setOpaque(false);
        riskContent.add(riskTop, BorderLayout.NORTH);
        riskContent.add(new JScrollPane(riskTable), BorderLayout.CENTER);
        riskContent.add(riskBottom, BorderLayout.SOUTH);
        riskCard.add(riskContent, BorderLayout.CENTER);

        // RIGHT: Module F - Pearson Correlation
        JPanel corrCard = createSectionPanel("Module F: Two-Stock Return Correlation");
        JPanel corrContent = new JPanel(new BorderLayout(0, 14));
        corrContent.setOpaque(false);

        JPanel corrSelectors = new JPanel(new GridLayout(2, 2, 10, 10));
        corrSelectors.setOpaque(false);
        cmbCorrStockA = new JComboBox<>();
        cmbCorrStockB = new JComboBox<>();
        cmbCorrStockA.setFont(FONT_BODY);
        cmbCorrStockB.setFont(FONT_BODY);
        cmbCorrStockA.addActionListener(e -> refreshCorrelationAnalysis());
        cmbCorrStockB.addActionListener(e -> refreshCorrelationAnalysis());

        corrSelectors.add(createLabel("First Stock (X):"));
        corrSelectors.add(cmbCorrStockA);
        corrSelectors.add(createLabel("Second Stock (Y):"));
        corrSelectors.add(cmbCorrStockB);

        JPanel corrResultBox = new JPanel(new BorderLayout(0, 8));
        corrResultBox.setBackground(BG_MAIN);
        corrResultBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel corrHeading = new JLabel("Pearson Correlation Coefficient (r)");
        corrHeading.setFont(FONT_SUBTITLE);
        corrHeading.setForeground(TEXT_SECONDARY);

        lblCorrValue = new JLabel("0.000");
        lblCorrValue.setFont(new Font(FONT_FAMILY, Font.BOLD, 28));
        lblCorrValue.setForeground(ACCENT_BLUE);

        txtCorrInterpretation = new JTextArea(6, 20);
        txtCorrInterpretation.setEditable(false);
        txtCorrInterpretation.setLineWrap(true);
        txtCorrInterpretation.setWrapStyleWord(true);
        txtCorrInterpretation.setFont(FONT_BODY);
        txtCorrInterpretation.setBackground(BG_MAIN);
        txtCorrInterpretation.setForeground(TEXT_PRIMARY);

        corrResultBox.add(corrHeading, BorderLayout.NORTH);
        corrResultBox.add(lblCorrValue, BorderLayout.CENTER);
        corrResultBox.add(txtCorrInterpretation, BorderLayout.SOUTH);

        JPanel scaleGuide = createSectionPanel("Correlation Reference Guide");
        JTextArea guideText = new JTextArea(
                "Formula:  r = Cov(X, Y) / (StdDev(X) × StdDev(Y))\n\n" +
                "•  r = +1.00 : Perfect positive linear correlation (move identically).\n" +
                "•  r =  0.00 : No linear correlation (movements are independent).\n" +
                "•  r = -1.00 : Perfect negative linear correlation (move inversely).\n\n" +
                "Date Alignment: Only trading dates available in both stocks' historical records are paired."
        );
        guideText.setEditable(false);
        guideText.setFont(FONT_BODY);
        guideText.setForeground(TEXT_PRIMARY);
        guideText.setBackground(CARD_BG);
        scaleGuide.add(guideText, BorderLayout.CENTER);

        corrContent.add(corrSelectors, BorderLayout.NORTH);
        corrContent.add(corrResultBox, BorderLayout.CENTER);
        corrContent.add(scaleGuide, BorderLayout.SOUTH);
        corrCard.add(corrContent, BorderLayout.CENTER);

        split.add(riskCard);
        split.add(corrCard);

        page.add(split, BorderLayout.CENTER);
        return page;
    }

    // =========================================================================
    // 6. SETTINGS & ABOUT VIEW
    // =========================================================================
    private JPanel createAboutPanel() {
        JPanel page = createPageContainer("Settings & About InvestWise",
                "Application architecture, data storage details, and sample data reset options.");

        JPanel card = createSectionPanel("About This Project");
        JTextArea info = new JTextArea(
                "Project Title: InvestWise — Investment Portfolio Risk & Diversification Analyzer\n" +
                "Course Context: Undergraduate Object-Oriented Programming (OOPS) Lab — Computational & Mathematics (CAM)\n\n" +
                "Core OOPS Principles Implemented:\n" +
                "1. Abstraction & Inheritance: Abstract 'Investment' class extended by 'Stock' and 'MutualFund' subclasses.\n" +
                "2. Polymorphism: Overridden getInvestmentType() and getSpecificDetails() methods dynamically invoked across tables.\n" +
                "3. Encapsulation: Private attributes with strict input validation inside setters.\n" +
                "4. Composition: 'Portfolio' manages a list of 'Investment' instances.\n" +
                "5. Java Collections: ArrayList for portfolio records, HashSet for mutual fund overlap intersection, LinkedHashMap for aligned time-series.\n\n" +
                "Disclaimer:\n" +
                "InvestWise is an educational analytical tool. Historical prices included in 'data/historical_prices.csv' are illustrative datasets for demonstrating statistical calculations. This software does not provide financial advice."
        );
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(FONT_BODY);
        info.setForeground(TEXT_PRIMARY);
        info.setBackground(CARD_BG);
        info.setBorder(new EmptyBorder(8, 8, 8, 8));
        card.add(info, BorderLayout.CENTER);

        JPanel actionsCard = createSectionPanel("Data Management");
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        btnRow.setOpaque(false);

        JButton btnReset = createDangerButton("Reset All CSV Data to Default Sample Dataset");
        btnReset.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to overwrite your current portfolio with the default sample dataset?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new File("data/investments.csv").delete();
                new File("data/fund_holdings.csv").delete();
                new File("data/historical_prices.csv").delete();
                DataManager.ensureSampleDataExists();
                this.fundHoldingsMap = DataManager.loadFundHoldings();
                this.historicalPricesMap = DataManager.loadHistoricalPrices();
                this.portfolio.setAllInvestments(DataManager.loadInvestments(fundHoldingsMap));
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Sample datasets have been restored.");
            }
        });
        btnRow.add(btnReset);
        actionsCard.add(btnRow, BorderLayout.CENTER);

        JPanel layout = new JPanel(new BorderLayout(0, 16));
        layout.setOpaque(false);
        layout.add(card, BorderLayout.CENTER);
        layout.add(actionsCard, BorderLayout.SOUTH);

        page.add(layout, BorderLayout.CENTER);
        return page;
    }

    // =========================================================================
    // DIALOGS & WORKFLOWS (ADD, EDIT, DELETE, CSV IMPORT, HOLDINGS)
    // =========================================================================

    private void showAddOrEditDialog(String forcedType, Investment existing) {
        boolean isEdit = (existing != null);
        String type = isEdit ? existing.getInvestmentType() : forcedType;

        JDialog dialog = new JDialog(this, (isEdit ? "Edit " : "Add ") + type + " Investment", true);
        dialog.setSize(440, 390);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        form.setBackground(CARD_BG);

        JTextField txtId = new JTextField(isEdit ? existing.getId() : portfolio.generateNextId(type));
        txtId.setEditable(!isEdit);
        JTextField txtName = new JTextField(isEdit ? existing.getName() : "");
        JTextField txtQty = new JTextField(isEdit ? String.valueOf(existing.getQuantity()) : "");
        JTextField txtBuy = new JTextField(isEdit ? String.valueOf(existing.getPurchasePrice()) : "");
        JTextField txtCur = new JTextField(isEdit ? String.valueOf(existing.getCurrentPrice()) : "");
        JTextField txtDate = new JTextField(isEdit ? existing.getPurchaseDate() : "2025-02-01");
        JTextField txtExtra = new JTextField();

        if (isEdit) {
            if (existing instanceof Stock) {
                txtExtra.setText(((Stock) existing).getTickerSymbol());
            } else if (existing instanceof MutualFund) {
                txtExtra.setText(((MutualFund) existing).getFundCategory());
            }
        } else {
            txtExtra.setText(type.equals("Stock") ? "NSE" : "Large Cap Equity");
        }

        form.add(createLabel("Investment ID:"));
        form.add(txtId);
        form.add(createLabel(type + " Name:"));
        form.add(txtName);
        form.add(createLabel(type.equals("Stock") ? "Quantity (Shares):" : "Units:"));
        form.add(txtQty);
        form.add(createLabel("Purchase Price (₹):"));
        form.add(txtBuy);
        form.add(createLabel("Current Price (₹):"));
        form.add(txtCur);
        form.add(createLabel(type.equals("Stock") ? "Ticker Symbol:" : "Fund Category:"));
        form.add(txtExtra);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(BG_MAIN);
        JButton btnCancel = createSecondaryButton("Cancel");
        JButton btnSave = createPrimaryButton(isEdit ? "Save Changes" : "Add Investment");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                double qty = parsePositiveDouble(txtQty.getText(), "Quantity/Units");
                double buy = parsePositiveDouble(txtBuy.getText(), "Purchase Price");
                double cur = parseNonNegativeDouble(txtCur.getText(), "Current Price");
                String date = txtDate.getText().trim();
                String extra = txtExtra.getText().trim();

                if (isEdit) {
                    existing.setName(name);
                    existing.setQuantity(qty);
                    existing.setPurchasePrice(buy);
                    existing.setCurrentPrice(cur);
                    existing.setPurchaseDate(date);
                    if (existing instanceof Stock) {
                        ((Stock) existing).setTickerSymbol(extra);
                    } else if (existing instanceof MutualFund) {
                        ((MutualFund) existing).setFundCategory(extra);
                    }
                } else {
                    if (type.equals("Stock")) {
                        portfolio.addInvestment(new Stock(id, name, qty, buy, cur, date, extra));
                    } else {
                        MutualFund mf = new MutualFund(id, name, qty, buy, cur, date, extra);
                        if (fundHoldingsMap.containsKey(name)) {
                            mf.setHoldings(fundHoldingsMap.get(name));
                        } else {
                            Set<String> defaultSet = new LinkedHashSet<>(Arrays.asList("Reliance Industries", "HDFC Bank", "Infosys"));
                            mf.setHoldings(defaultSet);
                            fundHoldingsMap.put(name, defaultSet);
                            DataManager.saveFundHoldings(fundHoldingsMap);
                        }
                        portfolio.addInvestment(mf);
                    }
                }

                DataManager.saveInvestments(portfolio.getAllInvestments());
                refreshAllViews();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Input Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnCancel);
        footer.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleEditSelectedInvestment() {
        int selectedRow = portfolioTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an investment row from the table to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = portfolioTableModel.getValueAt(selectedRow, 0).toString();
        Investment inv = portfolio.findById(id);
        if (inv != null) {
            showAddOrEditDialog(inv.getInvestmentType(), inv);
        }
    }

    private void handleDeleteSelectedInvestment() {
        int selectedRow = portfolioTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an investment row from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = portfolioTableModel.getValueAt(selectedRow, 0).toString();
        String name = portfolioTableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete investment '" + name + "' (" + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            portfolio.removeInvestment(id);
            DataManager.saveInvestments(portfolio.getAllInvestments());
            refreshAllViews();
        }
    }

    /**
     * User CSV Import Feature: Browse -> Validate -> Preview Dialog -> Confirm or Cancel -> Save Locally.
     */
    private void handleImportCsvWorkflow() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Select Investment CSV File to Import");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int choice = chooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        DataManager.CsvValidationReport report = DataManager.validateImportCsv(selectedFile, portfolio);

        if (!report.isHeaderValid()) {
            JOptionPane.showMessageDialog(this,
                    report.getHeaderErrorMessage(),
                    "CSV Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showCsvPreviewDialog(selectedFile.getName(), report);
    }

    private void showCsvPreviewDialog(String fileName, DataManager.CsvValidationReport report) {
        JDialog previewDialog = new JDialog(this, "CSV Import Preview — " + fileName, true);
        previewDialog.setSize(900, 480);
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setLayout(new BorderLayout(0, 12));
        previewDialog.getContentPane().setBackground(BG_MAIN);

        int validCount = report.getValidRowCount();
        int totalCount = report.getRows().size();

        JLabel summaryLbl = new JLabel(String.format(
                "File: %s   |   Total Rows: %d   |   Valid to Import: %d   |   Skipped/Invalid: %d",
                fileName, totalCount, validCount, (totalCount - validCount)));
        summaryLbl.setFont(FONT_BOLD);
        summaryLbl.setForeground(TEXT_PRIMARY);
        summaryLbl.setBorder(new EmptyBorder(14, 16, 4, 16));

        String[] cols = {"Row #", "ID", "Name", "Type", "Qty / Units", "Buy Price", "Current Price", "Date", "Status / Validation"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (DataManager.CsvImportRow row : report.getRows()) {
            model.addRow(new Object[]{
                row.getRowNumber(),
                row.getId(),
                row.getName(),
                row.getType(),
                row.getQuantity(),
                Investment.formatINR(row.getPurchasePrice()),
                Investment.formatINR(row.getCurrentPrice()),
                row.getPurchaseDate(),
                row.getStatus()
            });
        }

        JTable previewTable = createStyledTable(model, -1, -1);
        previewTable.getColumnModel().getColumn(8).setPreferredWidth(230);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(CARD_BG);
        JButton btnCancel = createSecondaryButton("Cancel Import");
        JButton btnConfirm = createPrimaryButton("Confirm Import (" + validCount + " Valid Records)");
        btnConfirm.setEnabled(validCount > 0);

        btnCancel.addActionListener(e -> previewDialog.dispose());
        btnConfirm.addActionListener(e -> {
            int imported = 0;
            for (DataManager.CsvImportRow r : report.getRows()) {
                if (r.isValidForImport()) {
                    if (r.getType().equalsIgnoreCase("Stock")) {
                        portfolio.addInvestment(new Stock(r.getId(), r.getName(), r.getQuantity(),
                                r.getPurchasePrice(), r.getCurrentPrice(), r.getPurchaseDate(), r.getExtraDetail()));
                    } else {
                        MutualFund mf = new MutualFund(r.getId(), r.getName(), r.getQuantity(),
                                r.getPurchasePrice(), r.getCurrentPrice(), r.getPurchaseDate(), r.getExtraDetail());
                        if (fundHoldingsMap.containsKey(r.getName())) {
                            mf.setHoldings(fundHoldingsMap.get(r.getName()));
                        } else {
                            Set<String> defaultHoldings = new LinkedHashSet<>(Arrays.asList("Reliance Industries", "Tata Consultancy Services", "HDFC Bank"));
                            mf.setHoldings(defaultHoldings);
                            fundHoldingsMap.put(r.getName(), defaultHoldings);
                        }
                        portfolio.addInvestment(mf);
                    }
                    imported++;
                }
            }
            DataManager.saveFundHoldings(fundHoldingsMap);
            DataManager.saveInvestments(portfolio.getAllInvestments());
            refreshAllViews();
            previewDialog.dispose();
            JOptionPane.showMessageDialog(this,
                    "Successfully imported and saved " + imported + " investment record(s).",
                    "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        footer.add(btnCancel);
        footer.add(btnConfirm);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(new EmptyBorder(0, 16, 0, 16));
        centerWrap.add(new JScrollPane(previewTable), BorderLayout.CENTER);

        previewDialog.add(summaryLbl, BorderLayout.NORTH);
        previewDialog.add(centerWrap, BorderLayout.CENTER);
        previewDialog.add(footer, BorderLayout.SOUTH);
        previewDialog.setVisible(true);
    }

    private void handleDownloadTemplateCsv() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Save Sample CSV Template");
        chooser.setSelectedFile(new File("investwise_import_template.csv"));
        int choice = chooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                File dest = chooser.getSelectedFile();
                DataManager.writeSampleImportTemplate(dest);
                JOptionPane.showMessageDialog(this,
                        "Sample CSV template saved to:\n" + dest.getAbsolutePath(),
                        "Template Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not save template: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showManageHoldingsDialog() {
        String selectedFund = (String) cmbFundA.getSelectedItem();
        if (selectedFund == null) return;

        Set<String> current = fundHoldingsMap.getOrDefault(selectedFund, new LinkedHashSet<>());
        String existingText = String.join(", ", current);

        String input = JOptionPane.showInputDialog(this,
                "Edit comma-separated stock holdings for '" + selectedFund + "':",
                existingText);

        if (input != null) {
            Set<String> updated = new LinkedHashSet<>();
            for (String part : input.split(",")) {
                if (!part.trim().isEmpty()) {
                    updated.add(part.trim());
                }
            }
            if (updated.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A mutual fund must have at least one holding.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fundHoldingsMap.put(selectedFund, updated);
            DataManager.saveFundHoldings(fundHoldingsMap);
            refreshOverlapComparison();
        }
    }

    // =========================================================================
    // DATA REFRESH & SYNCHRONIZATION
    // =========================================================================

    private void refreshAllViews() {
        List<Investment> all = portfolio.getAllInvestments();

        // 1. Update Dashboard Metrics
        double totalInvested = ReturnCalculator.calculateTotalInvested(all);
        double currentVal = ReturnCalculator.calculateCurrentPortfolioValue(all);
        double totalPL = ReturnCalculator.calculateTotalProfitOrLoss(all);
        double returnPct = ReturnCalculator.calculateOverallReturnPercentage(all);

        lblDashInvested.setText(Investment.formatINR(totalInvested));
        lblDashCurrent.setText(Investment.formatINR(currentVal));
        lblDashProfitLoss.setText((totalPL >= 0 ? "+" : "") + Investment.formatINR(totalPL));
        lblDashProfitLoss.setForeground(totalPL >= 0 ? POSITIVE_GREEN : NEGATIVE_RED);

        lblDashReturnPct.setText(String.format("%s%.2f%%", (returnPct >= 0 ? "+" : ""), returnPct));
        lblDashReturnPct.setForeground(returnPct >= 0 ? POSITIVE_GREEN : NEGATIVE_RED);

        dashTableModel.setRowCount(0);
        for (Investment inv : all) {
            double pl = inv.getProfitOrLoss();
            double pct = inv.getReturnPercentage();
            dashTableModel.addRow(new Object[]{
                inv.getId(),
                inv.getName(),
                inv.getInvestmentType(),
                Investment.formatINR(inv.getInitialInvestment()),
                Investment.formatINR(inv.getCurrentValue()),
                (pl >= 0 ? "+" : "") + Investment.formatINR(pl),
                String.format("%s%.2f%%", (pct >= 0 ? "+" : ""), pct)
            });
        }

        Map<String, Double> typeAllocation = portfolio.getAssetAllocationByType();
        dashDonutChart.setData(typeAllocation);

        // 2. Update Portfolio Table
        refreshPortfolioTable();

        // 3. Update Asset Allocation View
        allocationDonutChart.setData(typeAllocation);
        lblAllocTotalValue.setText("Total Portfolio Market Value: " + Investment.formatINR(currentVal));

        allocationClassTableModel.setRowCount(0);
        Map<String, Double> pctMap = portfolio.getAssetAllocationPercentages();
        for (Map.Entry<String, Double> entry : typeAllocation.entrySet()) {
            double pct = pctMap.getOrDefault(entry.getKey(), 0.0);
            allocationClassTableModel.addRow(new Object[]{
                entry.getKey(),
                Investment.formatINR(entry.getValue()),
                String.format("%.2f%%", pct)
            });
        }

        allocationHoldingTableModel.setRowCount(0);
        for (Investment inv : all) {
            double share = currentVal > 0 ? (inv.getCurrentValue() / currentVal) * 100.0 : 0.0;
            allocationHoldingTableModel.addRow(new Object[]{
                inv.getName(),
                inv.getInvestmentType(),
                Investment.formatINR(inv.getCurrentValue()),
                String.format("%.2f%%", share)
            });
        }

        // 4. Sync Fund Overlap Dropdowns
        syncOverlapDropdowns();

        // 5. Sync Risk & Correlation Dropdowns
        syncStockDropdowns();
    }

    private void refreshPortfolioTable() {
        if (portfolioTableModel == null) return;
        String query = txtSearch != null ? txtSearch.getText() : "";
        String filter = cmbTypeFilter != null && cmbTypeFilter.getSelectedItem() != null
                ? cmbTypeFilter.getSelectedItem().toString() : "All";

        List<Investment> filtered = portfolio.searchAndFilter(query, filter);
        portfolioTableModel.setRowCount(0);

        for (Investment inv : filtered) {
            double pl = inv.getProfitOrLoss();
            double pct = inv.getReturnPercentage();
            portfolioTableModel.addRow(new Object[]{
                inv.getId(),
                inv.getName(),
                inv.getInvestmentType(),
                inv.getSpecificDetails(),
                String.format("%.2f", inv.getQuantity()),
                Investment.formatINR(inv.getPurchasePrice()),
                Investment.formatINR(inv.getCurrentPrice()),
                inv.getPurchaseDate(),
                Investment.formatINR(inv.getInitialInvestment()),
                Investment.formatINR(inv.getCurrentValue()),
                (pl >= 0 ? "+" : "") + Investment.formatINR(pl),
                String.format("%s%.2f%%", (pct >= 0 ? "+" : ""), pct)
            });
        }
    }

    private void syncOverlapDropdowns() {
        if (cmbFundA == null || cmbFundB == null) return;
        String prevA = (String) cmbFundA.getSelectedItem();
        String prevB = (String) cmbFundB.getSelectedItem();

        List<String> funds = new ArrayList<>(fundHoldingsMap.keySet());
        cmbFundA.removeAllItems();
        cmbFundB.removeAllItems();
        for (String f : funds) {
            cmbFundA.addItem(f);
            cmbFundB.addItem(f);
        }

        if (prevA != null && funds.contains(prevA)) {
            cmbFundA.setSelectedItem(prevA);
        } else if (!funds.isEmpty()) {
            cmbFundA.setSelectedIndex(0);
        }

        if (prevB != null && funds.contains(prevB)) {
            cmbFundB.setSelectedItem(prevB);
        } else if (funds.size() > 1) {
            cmbFundB.setSelectedIndex(1);
        } else if (!funds.isEmpty()) {
            cmbFundB.setSelectedIndex(0);
        }

        refreshOverlapComparison();
    }

    private void refreshOverlapComparison() {
        if (cmbFundA == null || cmbFundB == null) return;
        String fundA = (String) cmbFundA.getSelectedItem();
        String fundB = (String) cmbFundB.getSelectedItem();
        if (fundA == null || fundB == null) return;

        OverlapDetector.OverlapResult res = OverlapDetector.analyzeOverlap(
                fundA, fundHoldingsMap.get(fundA),
                fundB, fundHoldingsMap.get(fundB));

        lblOverlapSummary.setText(res.getCommonCount() + " Common Stocks");
        lblOverlapPctA.setText(String.format("%.1f%% (%d of %d)", res.getOverlapRelativeToA(), res.getCommonCount(), res.getFundAHoldings().size()));
        lblOverlapPctB.setText(String.format("%.1f%% (%d of %d)", res.getOverlapRelativeToB(), res.getCommonCount(), res.getFundBHoldings().size()));

        listModelFundA.clear();
        for (String s : res.getFundAHoldings()) listModelFundA.addElement(s);

        listModelCommon.clear();
        if (res.getCommonHoldings().isEmpty()) {
            listModelCommon.addElement("No overlapping holdings found.");
        } else {
            for (String s : res.getCommonHoldings()) listModelCommon.addElement(s);
        }

        listModelFundB.clear();
        for (String s : res.getFundBHoldings()) listModelFundB.addElement(s);
    }

    private void syncStockDropdowns() {
        if (cmbRiskStock == null || cmbCorrStockA == null || cmbCorrStockB == null) return;
        if (cmbRiskStock.getItemCount() == 0) {
            List<String> stocks = new ArrayList<>(historicalPricesMap.keySet());
            for (String s : stocks) {
                cmbRiskStock.addItem(s);
                cmbCorrStockA.addItem(s);
                cmbCorrStockB.addItem(s);
            }
            if (stocks.size() > 1) {
                cmbCorrStockB.setSelectedIndex(1);
            }
        }
        refreshRiskAnalysis();
        refreshCorrelationAnalysis();
    }

    private void refreshRiskAnalysis() {
        if (cmbRiskStock == null) return;
        String stock = (String) cmbRiskStock.getSelectedItem();
        if (stock == null || !historicalPricesMap.containsKey(stock)) return;

        List<RiskAnalyzer.PricePoint> prices = historicalPricesMap.get(stock);
        List<Double> returns = RiskAnalyzer.calculateDailyReturns(prices);

        double mean = RiskAnalyzer.calculateMeanReturn(returns);
        double variance = RiskAnalyzer.calculateSampleVariance(returns);
        double stdDev = RiskAnalyzer.calculateStandardDeviation(returns);

        lblMeanDailyReturn.setText(String.format("%s%.3f%%", (mean >= 0 ? "+" : ""), mean * 100.0));
        lblMeanDailyReturn.setForeground(mean >= 0 ? POSITIVE_GREEN : NEGATIVE_RED);
        lblSampleVariance.setText(String.format("%.6f", variance));
        lblStdDeviation.setText(String.format("%.3f%%", stdDev * 100.0));

        riskTableModel.setRowCount(0);
        for (int i = 0; i < prices.size(); i++) {
            RiskAnalyzer.PricePoint pt = prices.get(i);
            String retStr = "— (Base Day)";
            if (i > 0) {
                double r = returns.get(i - 1) * 100.0;
                retStr = String.format("%s%.2f%%", (r >= 0 ? "+" : ""), r);
            }
            riskTableModel.addRow(new Object[]{
                pt.getDate(),
                Investment.formatINR(pt.getClosePrice()),
                retStr
            });
        }

        riskBarChart.setReturns(returns);
    }

    private void refreshCorrelationAnalysis() {
        if (cmbCorrStockA == null || cmbCorrStockB == null) return;
        String stockA = (String) cmbCorrStockA.getSelectedItem();
        String stockB = (String) cmbCorrStockB.getSelectedItem();
        if (stockA == null || stockB == null) return;

        try {
            CorrelationCalculator.CorrelationResult res = CorrelationCalculator.analyzeCorrelation(
                    stockA, historicalPricesMap.get(stockA),
                    stockB, historicalPricesMap.get(stockB));

            lblCorrValue.setText(String.format("%s%.4f", (res.getCorrelation() >= 0 ? "+" : ""), res.getCorrelation()));
            txtCorrInterpretation.setText(
                    "Selected Pair: " + res.getStockA() + " vs. " + res.getStockB() + "\n" +
                    "Aligned Trading Days: " + res.getAlignedPriceCount() + " (" + res.getReturnObservations() + " daily return pairs)\n\n" +
                    res.getInterpretation()
            );
        } catch (Exception ex) {
            lblCorrValue.setText("N/A");
            txtCorrInterpretation.setText("Calculation Notice: " + ex.getMessage());
        }
    }

    // =========================================================================
    // CUSTOM GRAPHICS2D CHARTS (ZERO EXTERNAL DEPENDENCIES)
    // =========================================================================

    /**
     * Clean anti-aliased Donut Chart for Asset Allocation.
     */
    private static class DonutChartPanel extends JPanel {
        private Map<String, Double> data = new LinkedHashMap<>();
        private final Color[] sliceColors = {
            new Color(0x34, 0x78, 0xC7),
            new Color(0x23, 0x86, 0x5B),
            new Color(0xD9, 0x82, 0x2B),
            new Color(0x7B, 0x61, 0xFF)
        };

        public DonutChartPanel() {
            setBackground(CARD_BG);
        }

        public void setData(Map<String, Double> newData) {
            this.data = new LinkedHashMap<>(newData);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = 0;
            for (double v : data.values()) total += v;

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w - 40, h - 90);
            size = Math.max(size, 110);
            int x = (w - size) / 2;
            int y = 16;

            if (total <= 0) {
                g2.setColor(BORDER_COLOR);
                g2.fillOval(x, y, size, size);
                g2.setColor(CARD_BG);
                g2.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);
                g2.setColor(TEXT_SECONDARY);
                g2.setFont(FONT_BODY);
                String msg = "No Portfolio Data";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, y + size / 2 + 5);
                g2.dispose();
                return;
            }

            double startAngle = 90.0;
            int colorIdx = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double val = entry.getValue();
                double angle = (val / total) * 360.0;
                g2.setColor(sliceColors[colorIdx % sliceColors.length]);
                g2.fill(new Arc2D.Double(x, y, size, size, startAngle, -angle, Arc2D.PIE));
                startAngle -= angle;
                colorIdx++;
            }

            // Inner cutout for Donut appearance
            int hole = (int) (size * 0.56);
            int hx = x + (size - hole) / 2;
            int hy = y + (size - hole) / 2;
            g2.setColor(CARD_BG);
            g2.fillOval(hx, hy, hole, hole);

            // Legend below donut
            int legendY = y + size + 24;
            int legendX = 24;
            colorIdx = 0;
            g2.setFont(FONT_BODY);
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double pct = (entry.getValue() / total) * 100.0;
                g2.setColor(sliceColors[colorIdx % sliceColors.length]);
                g2.fillRect(legendX, legendY - 10, 12, 12);

                g2.setColor(TEXT_PRIMARY);
                String label = String.format("%s: %.1f%%", entry.getKey(), pct);
                g2.drawString(label, legendX + 18, legendY);
                legendX += g2.getFontMetrics().stringWidth(label) + 38;
                colorIdx++;
            }

            g2.dispose();
        }
    }

    /**
     * Daily return bar chart showing positive vs negative daily volatility.
     */
    private static class ReturnBarChartPanel extends JPanel {
        private List<Double> returns = new ArrayList<>();

        public ReturnBarChartPanel() {
            setBackground(BG_MAIN);
            setBorder(new LineBorder(BORDER_COLOR, 1));
        }

        public void setReturns(List<Double> newReturns) {
            this.returns = new ArrayList<>(newReturns);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int midY = h / 2;

            g2.setColor(TEXT_SECONDARY);
            g2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 11));
            g2.drawString("Daily Return Fluctuations (+ / -)", 10, 16);

            g2.setColor(BORDER_COLOR);
            g2.drawLine(12, midY, w - 12, midY);

            if (returns.isEmpty()) {
                g2.dispose();
                return;
            }

            double maxAbs = 0.005;
            for (double r : returns) {
                maxAbs = Math.max(maxAbs, Math.abs(r));
            }

            int n = returns.size();
            int availW = w - 32;
            int barW = Math.max(4, (availW / n) - 3);
            int maxBarH = (h / 2) - 24;

            for (int i = 0; i < n; i++) {
                double r = returns.get(i);
                int barH = (int) ((Math.abs(r) / maxAbs) * maxBarH);
                barH = Math.max(2, barH);
                int bx = 16 + i * (barW + 3);
                if (r >= 0) {
                    g2.setColor(POSITIVE_GREEN);
                    g2.fillRect(bx, midY - barH, barW, barH);
                } else {
                    g2.setColor(NEGATIVE_RED);
                    g2.fillRect(bx, midY, barW, barH);
                }
            }
            g2.dispose();
        }
    }

    // =========================================================================
    // REUSABLE UI HELPERS
    // =========================================================================

    private JPanel createPageContainer(String title, String description) {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(BG_MAIN);
        page.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(FONT_SUBTITLE);
        lblDesc.setForeground(TEXT_SECONDARY);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblDesc);

        page.add(header, BorderLayout.NORTH);
        return page;
    }

    private JPanel createSectionPanel(String heading) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lbl = new JLabel(heading);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(TEXT_PRIMARY);
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private JPanel createMetricCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_SECONDARY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMiniStatBox(String title, JLabel valLabel) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setBackground(BG_MAIN);
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font(FONT_FAMILY, Font.PLAIN, 11));
        t.setForeground(TEXT_SECONDARY);
        valLabel.setFont(FONT_BOLD);
        valLabel.setForeground(TEXT_PRIMARY);
        box.add(t, BorderLayout.NORTH);
        box.add(valLabel, BorderLayout.CENTER);
        return box;
    }

    private JPanel createListCard(String title, DefaultListModel<String> model, Color textColor) {
        JPanel card = createSectionPanel(title);
        JList<String> list = new JList<>(model);
        list.setFont(FONT_BODY);
        list.setForeground(textColor);
        list.setFixedCellHeight(26);
        card.add(new JScrollPane(list), BorderLayout.CENTER);
        return card;
    }

    private JLabel createMetricValueLabel() {
        JLabel lbl = new JLabel("₹0.00");
        lbl.setFont(FONT_METRIC);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    private JTable createStyledTable(DefaultTableModel model, int plColIndex, int pctColIndex) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(0xE8, 0xF0, 0xFE));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(BG_MAIN);
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                String text = value != null ? value.toString() : "";
                if (column == plColIndex || column == pctColIndex) {
                    if (text.startsWith("+")) {
                        c.setForeground(POSITIVE_GREEN);
                        c.setFont(FONT_BOLD);
                    } else if (text.startsWith("-")) {
                        c.setForeground(NEGATIVE_RED);
                        c.setFont(FONT_BOLD);
                    } else {
                        c.setForeground(TEXT_PRIMARY);
                    }
                } else {
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };

        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(colorRenderer);
        }

        return table;
    }

    private JButton createPrimaryButton(String text) {
        return createStyledButton(text, ACCENT_BLUE, Color.WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return createStyledButton(text, new Color(0xE3, 0xE8, 0xEE), TEXT_PRIMARY);
    }

    private JButton createDangerButton(String text) {
        return createStyledButton(text, NEGATIVE_RED, Color.WHITE);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));

        Color hoverBg = bg.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bg);
            }
        });
        return btn;
    }

    private double parsePositiveDouble(String raw, String fieldName) {
        try {
            double v = Double.parseDouble(raw.trim());
            if (v <= 0) throw new IllegalArgumentException(fieldName + " must be greater than zero.");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private double parseNonNegativeDouble(String raw, String fieldName) {
        try {
            double v = Double.parseDouble(raw.trim());
            if (v < 0) throw new IllegalArgumentException(fieldName + " cannot be negative.");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }
}
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for InvestWise: Investment Portfolio Risk & Diversification Analyzer.
 */
public class Main {
    public static void main(String[] args) {
        // Ensure sample CSV files exist before starting GUI
        DataManager.ensureSampleDataExists();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // Default look and feel fallback
            }
            Dashboard app = new Dashboard();
            app.setVisible(true);
        });
    }
}
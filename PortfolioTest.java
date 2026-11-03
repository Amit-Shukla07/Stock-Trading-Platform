import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for Portfolio holding operations and P/L calculations.
 */
public class PortfolioTest {

    public static void runAll() {
        System.out.println(Colors.BOLD_CYAN + "Running PortfolioTest..." + Colors.RESET);
        testBuyInitialStock();
        testWeightedAverageBuyPrice();
        testPartialSellStock();
        testCompleteSellStock();
        testSellMoreThanOwnedFails();
        testUnrealizedProfitLoss();
        System.out.println();
    }

    private static void testBuyInitialStock() {
        Portfolio portfolio = new Portfolio();
        portfolio.buyStock("AAPL", 10, 150.00);

        TestRunner.assertTrue(portfolio.ownsStock("AAPL"), "Portfolio should own AAPL");
        TestRunner.assertEquals(10, portfolio.getQuantity("AAPL"), "Quantity should be 10");
        TestRunner.assertEquals(150.00, portfolio.getAverageBuyPrice("AAPL"), 0.001, "Average price should be 150.00");
        TestRunner.assertEquals(1500.00, portfolio.getTotalInvestment(), 0.001, "Total investment should be 1500.00");
        TestRunner.pass("testBuyInitialStock");
    }

    private static void testWeightedAverageBuyPrice() {
        Portfolio portfolio = new Portfolio();
        // 10 shares @ $100 = $1,000
        portfolio.buyStock("GOOGL", 10, 100.00);
        // 10 more shares @ $200 = $2,000 -> Total $3,000 for 20 shares = $150 average
        portfolio.buyStock("GOOGL", 10, 200.00);

        TestRunner.assertEquals(20, portfolio.getQuantity("GOOGL"), "Total quantity should be 20");
        TestRunner.assertEquals(150.00, portfolio.getAverageBuyPrice("GOOGL"), 0.001, "Weighted average price should be $150.00");
        TestRunner.assertEquals(3000.00, portfolio.getTotalInvestment(), 0.001, "Total investment should be $3000.00");
        TestRunner.pass("testWeightedAverageBuyPrice");
    }

    private static void testPartialSellStock() {
        Portfolio portfolio = new Portfolio();
        portfolio.buyStock("MSFT", 10, 200.00);

        boolean sold = portfolio.sellStock("MSFT", 4);
        TestRunner.assertTrue(sold, "Partial sell should succeed");
        TestRunner.assertEquals(6, portfolio.getQuantity("MSFT"), "Remaining quantity should be 6");
        TestRunner.assertEquals(200.00, portfolio.getAverageBuyPrice("MSFT"), 0.001, "Average buy price should remain $200.00");
        TestRunner.pass("testPartialSellStock");
    }

    private static void testCompleteSellStock() {
        Portfolio portfolio = new Portfolio();
        portfolio.buyStock("NFLX", 5, 500.00);

        boolean sold = portfolio.sellStock("NFLX", 5);
        TestRunner.assertTrue(sold, "Selling entire holding should succeed");
        TestRunner.assertFalse(portfolio.ownsStock("NFLX"), "NFLX should no longer be in portfolio");
        TestRunner.assertEquals(0, portfolio.getQuantity("NFLX"), "Quantity should be 0");
        TestRunner.pass("testCompleteSellStock");
    }

    private static void testSellMoreThanOwnedFails() {
        Portfolio portfolio = new Portfolio();
        portfolio.buyStock("META", 3, 300.00);

        boolean sold = portfolio.sellStock("META", 5);
        TestRunner.assertFalse(sold, "Selling more than owned should fail");
        TestRunner.assertEquals(3, portfolio.getQuantity("META"), "Quantity should remain unchanged");
        TestRunner.pass("testSellMoreThanOwnedFails");
    }

    private static void testUnrealizedProfitLoss() {
        Portfolio portfolio = new Portfolio();
        portfolio.buyStock("AAPL", 10, 200.00); // Invested: $2000

        Map<String, Stock> market = new HashMap<>();
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 250.00)); // Current value: $2500

        double currentValue = portfolio.getCurrentValue(market);
        double unrealizedPL = portfolio.getProfitLoss(market);

        TestRunner.assertEquals(2500.00, currentValue, 0.001, "Current value should be $2500.00");
        TestRunner.assertEquals(500.00, unrealizedPL, 0.001, "Unrealized profit should be +$500.00");
        TestRunner.pass("testUnrealizedProfitLoss");
    }
}

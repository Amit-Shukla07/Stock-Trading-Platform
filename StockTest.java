/**
 * Unit tests for Stock domain model.
 */
public class StockTest {

    public static void runAll() {
        System.out.println(Colors.BOLD_CYAN + "Running StockTest..." + Colors.RESET);
        testInitialStockProperties();
        testPriceChangeCalculation();
        testPriceHistoryRollingBuffer();
        testNegativePriceRejected();
        System.out.println();
    }

    private static void testInitialStockProperties() {
        Stock stock = new Stock("AAPL", "Apple Inc.", 225.00);
        TestRunner.assertEquals("AAPL", stock.getSymbol(), "Stock symbol should match");
        TestRunner.assertEquals("Apple Inc.", stock.getCompanyName(), "Company name should match");
        TestRunner.assertEquals(225.00, stock.getPrice(), 0.001, "Initial price should match");
        TestRunner.pass("testInitialStockProperties");
    }

    private static void testPriceChangeCalculation() {
        Stock stock = new Stock("MSFT", "Microsoft Corp.", 500.00);
        stock.setPrice(550.00); // +10%
        TestRunner.assertEquals(10.0, stock.getChangePercent(), 0.01, "Price change should be +10%");

        stock.setPrice(495.00); // 495 vs 550 = -10%
        TestRunner.assertEquals(-10.0, stock.getChangePercent(), 0.01, "Price change should be -10%");
        TestRunner.pass("testPriceChangeCalculation");
    }

    private static void testPriceHistoryRollingBuffer() {
        Stock stock = new Stock("TSLA", "Tesla Inc.", 100.00);
        // Add 7 prices (max buffer is 6)
        stock.setPrice(110.00);
        stock.setPrice(120.00);
        stock.setPrice(130.00);
        stock.setPrice(140.00);
        stock.setPrice(150.00);
        stock.setPrice(160.00);

        TestRunner.assertEquals(6, stock.getPriceHistory().size(), "History size should not exceed 6");
        TestRunner.assertEquals(110.00, stock.getPriceHistory().getFirst(), 0.001, "Oldest price in buffer should be 110.00");
        TestRunner.assertEquals(160.00, stock.getPriceHistory().getLast(), 0.001, "Newest price should be 160.00");
        TestRunner.pass("testPriceHistoryRollingBuffer");
    }

    private static void testNegativePriceRejected() {
        Stock stock = new Stock("AMZN", "Amazon.com Inc.", 200.00);
        stock.setPrice(-50.00);
        TestRunner.assertEquals(200.00, stock.getPrice(), 0.001, "Negative price update should be ignored");
        TestRunner.pass("testNegativePriceRejected");
    }
}

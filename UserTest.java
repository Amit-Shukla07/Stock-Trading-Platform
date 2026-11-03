/**
 * Unit tests for User balance, trade validation, and profit/loss calculation.
 */
public class UserTest {

    public static void runAll() {
        System.out.println(Colors.BOLD_CYAN + "Running UserTest..." + Colors.RESET);
        testInitialUserProperties();
        testSuccessfulBuyStock();
        testInsufficientFundsPrevented();
        testSuccessfulSellStockAndRealizedPL();
        testSellUnownedStockPrevented();
        System.out.println();
    }

    private static void testInitialUserProperties() {
        User user = new User("Trader1", 100000.00);
        TestRunner.assertEquals("Trader1", user.getUsername(), "Username should match");
        TestRunner.assertEquals(100000.00, user.getBalance(), 0.001, "Initial balance should be 100,000.00");
        TestRunner.assertEquals(0.0, user.getRealizedProfitLoss(), 0.001, "Initial realized P/L should be 0.0");
        TestRunner.assertEquals(0, user.getTransactions().size(), "Transactions list should start empty");
        TestRunner.pass("testInitialUserProperties");
    }

    private static void testSuccessfulBuyStock() {
        User user = new User("Trader2", 10000.00);
        Stock stock = new Stock("AAPL", "Apple Inc.", 200.00);

        boolean success = user.buyStock(stock, 10); // $2,000
        TestRunner.assertTrue(success, "Buy should succeed");
        TestRunner.assertEquals(8000.00, user.getBalance(), 0.001, "Balance should decrease to 8,000.00");
        TestRunner.assertEquals(10, user.getPortfolio().getQuantity("AAPL"), "Portfolio should hold 10 AAPL");
        TestRunner.assertEquals(1, user.getTransactions().size(), "One transaction should be recorded");
        TestRunner.assertEquals("BUY", user.getTransactions().get(0).getType(), "Transaction type should be BUY");
        TestRunner.pass("testSuccessfulBuyStock");
    }

    private static void testInsufficientFundsPrevented() {
        User user = new User("Trader3", 500.00);
        Stock stock = new Stock("MSFT", "Microsoft Corp.", 600.00);

        boolean success = user.buyStock(stock, 1);
        TestRunner.assertFalse(success, "Buy should fail due to insufficient funds");
        TestRunner.assertEquals(500.00, user.getBalance(), 0.001, "Balance should remain intact");
        TestRunner.assertEquals(0, user.getPortfolio().getQuantity("MSFT"), "No shares should be added");
        TestRunner.assertEquals(0, user.getTransactions().size(), "No transaction should be recorded");
        TestRunner.pass("testInsufficientFundsPrevented");
    }

    private static void testSuccessfulSellStockAndRealizedPL() {
        User user = new User("Trader4", 10000.00);
        Stock stock = new Stock("TSLA", "Tesla Inc.", 100.00);

        user.buyStock(stock, 10); // Cost: $1,000, balance: $9,000

        // Price rises to $150
        stock.setPrice(150.00);

        boolean sold = user.sellStock(stock, 5); // Sell 5 @ $150 = $750. Profit = ($150 - $100) * 5 = +$250
        TestRunner.assertTrue(sold, "Sell should succeed");
        TestRunner.assertEquals(9750.00, user.getBalance(), 0.001, "Balance should be 9000 + 750 = $9,750");
        TestRunner.assertEquals(250.00, user.getRealizedProfitLoss(), 0.001, "Realized P/L should be +$250.00");
        TestRunner.assertEquals(5, user.getPortfolio().getQuantity("TSLA"), "Remaining quantity should be 5");
        TestRunner.assertEquals(2, user.getTransactions().size(), "Two transactions should be logged");
        TestRunner.assertEquals("SELL", user.getTransactions().get(1).getType(), "Second transaction should be SELL");
        TestRunner.pass("testSuccessfulSellStockAndRealizedPL");
    }

    private static void testSellUnownedStockPrevented() {
        User user = new User("Trader5", 10000.00);
        Stock stock = new Stock("AMZN", "Amazon.com Inc.", 200.00);

        boolean sold = user.sellStock(stock, 5);
        TestRunner.assertFalse(sold, "Selling unowned stock should fail");
        TestRunner.pass("testSellUnownedStockPrevented");
    }
}


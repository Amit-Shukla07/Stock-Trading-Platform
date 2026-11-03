import java.time.LocalDateTime;

/**
 * Unit tests for Transaction record generation and CSV serialization.
 */
public class TransactionTest {

    public static void runAll() {
        System.out.println(Colors.BOLD_CYAN + "Running TransactionTest..." + Colors.RESET);
        testTransactionCalculations();
        testCsvExportFormat();
        System.out.println();
    }

    private static void testTransactionCalculations() {
        Transaction t = new Transaction("BUY", "AAPL", 10, 150.00, 0.0);
        TestRunner.assertEquals("BUY", t.getType(), "Type should be BUY");
        TestRunner.assertEquals("AAPL", t.getStockSymbol(), "Symbol should be AAPL");
        TestRunner.assertEquals(10, t.getQuantity(), "Quantity should be 10");
        TestRunner.assertEquals(150.00, t.getPrice(), 0.001, "Price should be 150.00");
        TestRunner.assertEquals(1500.00, t.getTotalAmount(), 0.001, "Total should be 10 * 150 = 1500.00");
        TestRunner.assertEquals(0.0, t.getProfitLoss(), 0.001, "BUY profit/loss should be 0.0");
        TestRunner.pass("testTransactionCalculations");
    }

    private static void testCsvExportFormat() {
        LocalDateTime fixedTime = LocalDateTime.of(2026, 11, 4, 14, 30, 0);
        Transaction t = new Transaction("SELL", "GOOGL", 5, 200.00, 250.00, fixedTime);

        String csv = t.toCsvLine();
        String expected = "SELL,GOOGL,5,200.00,1000.00,250.00,04-11-2026 14:30:00";
        TestRunner.assertEquals(expected, csv, "CSV output must match comma-separated standard");
        TestRunner.pass("testCsvExportFormat");
    }
}


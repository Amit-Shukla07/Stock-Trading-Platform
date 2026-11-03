/**
 * Standalone Unit Test Runner for Stock Trading Platform.
 *
 * Runs all unit tests with color-coded terminal assertions,
 * detailed failure reports, and summary metrics.
 * Requires zero external dependencies.
 *
 * Run via:
 *   javac *.java
 *   java TestRunner
 */
public class TestRunner {

    private static int passedCount = 0;
    private static int failedCount = 0;

    public static void main(String[] args) {
        System.out.println("\n" + Colors.BOLD_CYAN +
            "╔════════════════════════════════════════════════════╗\n" +
            "║        🧪  STOCK TRADING PLATFORM TEST RUNNER      ║\n" +
            "╚════════════════════════════════════════════════════╝\n" +
            Colors.RESET);

        long startTime = System.currentTimeMillis();

        StockTest.runAll();
        PortfolioTest.runAll();
        UserTest.runAll();
        TransactionTest.runAll();

        long elapsed = System.currentTimeMillis() - startTime;
        int total = passedCount + failedCount;

        System.out.println(Colors.CYAN + "─".repeat(52) + Colors.RESET);
        if (failedCount == 0) {
            System.out.println(Colors.BOLD_GREEN +
                String.format("  ✔  ALL %d TESTS PASSED (%d ms)", total, elapsed) +
                Colors.RESET);
        } else {
            System.out.println(Colors.BOLD_RED +
                String.format("  ✖  TESTS FAILED: %d passed, %d failed (%d ms)", passedCount, failedCount, elapsed) +
                Colors.RESET);
            System.exit(1);
        }
        System.out.println(Colors.CYAN + "─".repeat(52) + "\n" + Colors.RESET);
    }

    public static void pass(String testName) {
        passedCount++;
        System.out.println("  " + Colors.BOLD_GREEN + "✔" + Colors.RESET + " " + testName + " " + Colors.GREEN + "PASSED" + Colors.RESET);
    }

    public static void fail(String testName, String message) {
        failedCount++;
        System.out.println("  " + Colors.BOLD_RED + "✖" + Colors.RESET + " " + testName + " " + Colors.BOLD_RED + "FAILED: " + message + Colors.RESET);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(String.format("%s [Expected: <%s>, Actual: <%s>]", message, expected, actual));
    }

    public static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) <= delta) return;
        throw new AssertionError(String.format("%s [Expected: <%.4f>, Actual: <%.4f>]", message, expected, actual));
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + " [Expected: <true>, Actual: <false>]");
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message + " [Expected: <false>, Actual: <true>]");
        }
    }
}

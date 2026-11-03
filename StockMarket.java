import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages the simulated stock market.
 * Provides market catalog display, price simulation,
 * and per-stock price history charts.
 */
public class StockMarket {

    private HashMap<String, Stock> stocks;
    private Random random;

    public StockMarket() {
        stocks = new HashMap<>();
        random = new Random();
        initializeStocks();
    }

    // ── Initialization ───────────────────────────────────────

    private void initializeStocks() {
        addStock(new Stock("AAPL",  "Apple Inc.",       225.00));
        addStock(new Stock("GOOGL", "Alphabet Inc.",    250.00));
        addStock(new Stock("MSFT",  "Microsoft Corp.",  500.00));
        addStock(new Stock("AMZN",  "Amazon.com Inc.",  230.00));
        addStock(new Stock("TSLA",  "Tesla Inc.",       450.00));
        addStock(new Stock("META",  "Meta Platforms",   600.00));
        addStock(new Stock("NFLX",  "Netflix Inc.",     700.00));
    }

    // ── Operations ───────────────────────────────────────────

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    public Map<String, Stock> getStocks() {
        return stocks;
    }

    // ── Display ──────────────────────────────────────────────

    /** Prints colorful market table with price change indicators. */
    public void displayMarket() {

        String line = "─".repeat(52);
        System.out.println("\n" + Colors.BOLD_CYAN + "┌" + "─".repeat(50) + "┐" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "│" + Colors.RESET +
            Colors.BOLD_WHITE + centerPad("📊  LIVE MARKET DATA", 50) + Colors.RESET +
            Colors.BOLD_CYAN + "│" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "└" + "─".repeat(50) + "┘" + Colors.RESET);

        System.out.printf(Colors.BOLD_WHITE +
            "  %-6s  %-22s  %-10s  %s%n" + Colors.RESET,
            "Symbol", "Company", "Price", "Change");
        System.out.println(Colors.CYAN + "  " + line + Colors.RESET);

        for (Stock stock : stocks.values()) {
            stock.displayStock();
        }

        System.out.println(Colors.CYAN + "  " + line + Colors.RESET);
    }

    /**
     * Simulates random price movement (±10%) for all stocks,
     * prints a change summary colored green/red per stock.
     */
    public void updateMarketPrices() {

        System.out.println("\n" + Colors.BOLD_YELLOW +
            "  🔄  Simulating market price changes..." + Colors.RESET);
        System.out.println(Colors.CYAN + "  " + "─".repeat(48) + Colors.RESET);

        for (Stock stock : stocks.values()) {
            double oldPrice = stock.getPrice();
            double change   = (random.nextDouble() * 0.10) - 0.05; // ±5%
            double newPrice = Math.max(1.0, oldPrice + (oldPrice * change));

            stock.setPrice(newPrice);

            double diff    = newPrice - oldPrice;
            String arrow   = diff >= 0 ? "▲" : "▼";
            String clr     = diff >= 0 ? Colors.BOLD_GREEN : Colors.BOLD_RED;

            System.out.printf(
                "  " + Colors.BOLD_WHITE + "%-6s" + Colors.RESET +
                "  $%-9.2f  %s%s $%.2f (%.2f%%)%s%n",
                stock.getSymbol(),
                newPrice,
                clr, arrow,
                Math.abs(diff),
                Math.abs(change * 100),
                Colors.RESET
            );
        }

        System.out.println(Colors.BOLD_GREEN +
            "\n  ✅  Market prices updated!" + Colors.RESET);
    }

    /** Displays mini ASCII price history chart for a given stock. */
    public void displayPriceHistory(String symbol) {
        Stock stock = getStock(symbol);
        if (stock == null) {
            System.out.println(Colors.RED + "  Stock not found: " + symbol + Colors.RESET);
            return;
        }
        stock.displayPriceHistory();
    }

    // ── Utility ──────────────────────────────────────────────

    private static String centerPad(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }
}

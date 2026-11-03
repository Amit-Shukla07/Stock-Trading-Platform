import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a tradable stock with symbol, company name,
 * current price, and a rolling price history (last 6 prices).
 */
public class Stock {

    private String symbol;
    private String companyName;
    private double price;
    private double previousPrice;

    // Stores last 6 prices for the ASCII trend chart
    private LinkedList<Double> priceHistory;
    private static final int MAX_HISTORY = 6;

    public Stock(String symbol, String companyName, double price) {
        this.symbol       = symbol;
        this.companyName  = companyName;
        this.price        = price;
        this.previousPrice = price;
        this.priceHistory = new LinkedList<>();
        this.priceHistory.add(price);
    }

    // ── Getters ──────────────────────────────────────────────

    public String getSymbol()      { return symbol; }
    public String getCompanyName() { return companyName; }
    public double getPrice()       { return price; }
    public double getPreviousPrice() { return previousPrice; }
    public LinkedList<Double> getPriceHistory() { return priceHistory; }

    // ── Price update ─────────────────────────────────────────

    public void setPrice(double price) {
        if (price >= 0) {
            this.previousPrice = this.price;
            this.price = price;
            // Rolling window — keep only last MAX_HISTORY prices
            if (priceHistory.size() >= MAX_HISTORY) {
                priceHistory.poll();
            }
            priceHistory.add(price);
        }
    }

    /** Percentage change from the previous price update. */
    public double getChangePercent() {
        if (previousPrice == 0) return 0;
        return ((price - previousPrice) / previousPrice) * 100;
    }

    // ── Display ──────────────────────────────────────────────

    /** One-line colored market row. */
    public void displayStock() {
        String changeStr = Colors.colorChange(getChangePercent());
        System.out.printf(
            Colors.BOLD_WHITE + "%-6s" + Colors.RESET +
            " %-25s " +
            Colors.BOLD_YELLOW + "$%9.2f" + Colors.RESET +
            "  %s%n",
            symbol,
            companyName,
            price,
            changeStr
        );
    }

    /**
     * Displays a mini ASCII bar chart of the last 6 prices.
     * Each bar is proportional to the price in the visible range.
     */
    public void displayPriceHistory() {

        if (priceHistory.size() < 2) {
            System.out.println(Colors.YELLOW + "  Not enough data yet." + Colors.RESET);
            return;
        }

        double min = priceHistory.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = priceHistory.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double range = max - min;

        System.out.println(Colors.BOLD_CYAN + "  Price History (" + symbol + "):" + Colors.RESET);

        int barWidth = 20;
        for (double p : priceHistory) {
            int filled = (range == 0) ? barWidth : (int) (((p - min) / range) * barWidth);
            String bar  = "█".repeat(Math.max(1, filled));
            String gap  = " ".repeat(barWidth - Math.max(1, filled));
            String color = (p >= previousPrice) ? Colors.GREEN : Colors.RED;
            System.out.printf("  %s%s%s%s  $%.2f%n",
                color, bar, Colors.RESET, gap, p);
        }
    }
}

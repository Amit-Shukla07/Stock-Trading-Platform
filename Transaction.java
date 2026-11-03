import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Records a single buy or sell trade with full details.
 * Supports colored terminal display and CSV export.
 */
public class Transaction {

    private String        type;
    private String        stockSymbol;
    private int           quantity;
    private double        price;
    private double        totalAmount;
    private double        profitLoss;
    private LocalDateTime dateTime;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // ── Constructors ─────────────────────────────────────────

    /** Used when creating a new live transaction. */
    public Transaction(String type, String stockSymbol,
                       int quantity, double price, double profitLoss) {
        this.type        = type;
        this.stockSymbol = stockSymbol;
        this.quantity    = quantity;
        this.price       = price;
        this.totalAmount = quantity * price;
        this.profitLoss  = profitLoss;
        this.dateTime    = LocalDateTime.now();
    }

    /** Used when loading a transaction from saved file. */
    public Transaction(String type, String stockSymbol,
                       int quantity, double price,
                       double profitLoss, LocalDateTime dateTime) {
        this.type        = type;
        this.stockSymbol = stockSymbol;
        this.quantity    = quantity;
        this.price       = price;
        this.totalAmount = quantity * price;
        this.profitLoss  = profitLoss;
        this.dateTime    = dateTime;
    }

    // ── Getters ──────────────────────────────────────────────

    public String        getType()        { return type; }
    public String        getStockSymbol() { return stockSymbol; }
    public int           getQuantity()    { return quantity; }
    public double        getPrice()       { return price; }
    public double        getTotalAmount() { return totalAmount; }
    public double        getProfitLoss()  { return profitLoss; }
    public LocalDateTime getDateTime()    { return dateTime; }

    // ── Display ──────────────────────────────────────────────

    /** Colored one-line transaction row. BUY = green, SELL = red/profit-colored. */
    public void displayTransaction() {
        String typeColor = type.equals("BUY") ? Colors.BOLD_GREEN : Colors.BOLD_RED;
        String plStr     = type.equals("BUY") ? Colors.YELLOW + "  —  " + Colors.RESET
                                              : Colors.colorPL(profitLoss);
        System.out.printf(
            typeColor + "%-5s" + Colors.RESET +
            "  " + Colors.BOLD_WHITE + "%-6s" + Colors.RESET +
            "  %-5d  " + Colors.YELLOW + "$%-10.2f" + Colors.RESET +
            "  $%-12.2f  %s  %s%n",
            type,
            stockSymbol,
            quantity,
            price,
            totalAmount,
            plStr,
            Colors.CYAN + dateTime.format(FORMATTER) + Colors.RESET
        );
    }

    /** CSV-formatted line for export. */
    public String toCsvLine() {
        return String.format("%s,%s,%d,%.2f,%.2f,%.2f,%s",
            type,
            stockSymbol,
            quantity,
            price,
            totalAmount,
            profitLoss,
            dateTime.format(FORMATTER)
        );
    }
}

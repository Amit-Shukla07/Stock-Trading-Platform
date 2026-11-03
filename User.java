import java.util.ArrayList;

/**
 * Represents a registered user with a cash balance,
 * stock portfolio, and transaction history.
 */
public class User {

    private String                username;
    private double                balance;
    private Portfolio             portfolio;
    private ArrayList<Transaction> transactions;
    private double                realizedProfitLoss;

    public User(String username, double balance) {
        this.username           = username;
        this.balance            = balance;
        this.portfolio          = new Portfolio();
        this.transactions       = new ArrayList<>();
        this.realizedProfitLoss = 0;
    }

    // ── Getters / Setters ────────────────────────────────────

    public String                 getUsername()           { return username; }
    public double                 getBalance()            { return balance; }
    public Portfolio              getPortfolio()          { return portfolio; }
    public ArrayList<Transaction> getTransactions()       { return transactions; }
    public double                 getRealizedProfitLoss() { return realizedProfitLoss; }

    public void setRealizedProfitLoss(double v) { this.realizedProfitLoss = v; }

    // ── Trade operations ─────────────────────────────────────

    /**
     * Buys the given quantity of stock if funds are sufficient.
     * Returns true on success, false on validation failure.
     */
    public boolean buyStock(Stock stock, int quantity) {

        if (quantity <= 0)                        return false;

        double totalCost = stock.getPrice() * quantity;
        if (totalCost > balance)                  return false;

        balance -= totalCost;

        portfolio.buyStock(stock.getSymbol(), quantity, stock.getPrice());

        transactions.add(new Transaction(
            "BUY", stock.getSymbol(), quantity, stock.getPrice(), 0
        ));

        return true;
    }

    /**
     * Sells the given quantity of stock if the user owns enough shares.
     * Returns true on success.
     */
    public boolean sellStock(Stock stock, int quantity) {

        if (quantity <= 0)                                         return false;
        if (!portfolio.ownsStock(stock.getSymbol()))               return false;
        if (portfolio.getQuantity(stock.getSymbol()) < quantity)   return false;

        double totalReceived = stock.getPrice() * quantity;
        double profitLoss    = calculateSellProfit(stock, quantity);

        portfolio.sellStock(stock.getSymbol(), quantity);

        balance            += totalReceived;
        realizedProfitLoss += profitLoss;

        transactions.add(new Transaction(
            "SELL", stock.getSymbol(), quantity, stock.getPrice(), profitLoss
        ));

        return true;
    }

    /** Calculates P/L for a potential sell based on average buy price. */
    public double calculateSellProfit(Stock stock, int quantity) {
        double avgPrice = portfolio.getAverageBuyPrice(stock.getSymbol());
        return (stock.getPrice() - avgPrice) * quantity;
    }

    // ── Display ──────────────────────────────────────────────

    /** Prints colorful account balance summary. */
    public void displayBalance() {
        System.out.println("\n" + Colors.BOLD_CYAN + "  ┌" + "─".repeat(38) + "┐" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "  │" + Colors.RESET +
            Colors.BOLD_WHITE + centerPad("💵  ACCOUNT BALANCE", 38) + Colors.RESET +
            Colors.BOLD_CYAN + "│" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "  └" + "─".repeat(38) + "┘" + Colors.RESET);

        System.out.printf("  %-22s " + Colors.BOLD_YELLOW + "$%.2f%n" + Colors.RESET,
            "Available Cash:", balance);
        System.out.printf("  %-22s %s%n",
            "Realized P/L:", Colors.colorPL(realizedProfitLoss));
    }

    /** Prints colored transaction history table. */
    public void displayTransactions() {

        if (transactions.isEmpty()) {
            System.out.println(Colors.YELLOW +
                "\n  No transactions yet. Start trading!" + Colors.RESET);
            return;
        }

        String divider = "─".repeat(76);
        System.out.println("\n" + Colors.BOLD_CYAN + "  ┌" + "─".repeat(74) + "┐" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "  │" + Colors.RESET +
            Colors.BOLD_WHITE + centerPad("📋  TRANSACTION HISTORY", 74) + Colors.RESET +
            Colors.BOLD_CYAN + "│" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "  └" + "─".repeat(74) + "┘" + Colors.RESET);

        System.out.printf(Colors.BOLD_WHITE +
            "  %-5s  %-6s  %-5s  %-12s  %-14s  %-12s  %s%n" + Colors.RESET,
            "Type", "Stock", "Qty", "Price", "Total", "P/L", "Date & Time");
        System.out.println(Colors.CYAN + "  " + divider + Colors.RESET);

        for (Transaction t : transactions) {
            t.displayTransaction();
        }
    }

    // ── Utility ──────────────────────────────────────────────

    private static String centerPad(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }
}

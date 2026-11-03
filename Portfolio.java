import java.util.HashMap;
import java.util.Map;

/**
 * Tracks all stock holdings for a user.
 * Calculates total investment value, current value, and P/L.
 */
public class Portfolio {

    private Map<String, Holding> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    // ── Trade operations ─────────────────────────────────────

    /** Updates average buy price using weighted average formula. */
    public void buyStock(String symbol, int quantity, double price) {

        if (holdings.containsKey(symbol)) {
            Holding h = holdings.get(symbol);
            double totalOld   = h.quantity * h.averageBuyPrice;
            double newInvest  = quantity * price;
            int    newQty     = h.quantity + quantity;
            h.averageBuyPrice = (totalOld + newInvest) / newQty;
            h.quantity        = newQty;
        } else {
            holdings.put(symbol, new Holding(quantity, price));
        }
    }

    public boolean sellStock(String symbol, int quantity) {

        if (!holdings.containsKey(symbol)) return false;

        Holding h = holdings.get(symbol);
        if (quantity > h.quantity)        return false;

        h.quantity -= quantity;

        if (h.quantity == 0) {
            holdings.remove(symbol);
        }

        return true;
    }

    // ── Queries ──────────────────────────────────────────────

    public boolean ownsStock(String symbol) {
        return holdings.containsKey(symbol);
    }

    public int getQuantity(String symbol) {
        if (!holdings.containsKey(symbol)) return 0;
        return holdings.get(symbol).quantity;
    }

    public double getAverageBuyPrice(String symbol) {
        if (!holdings.containsKey(symbol)) return 0;
        return holdings.get(symbol).averageBuyPrice;
    }

    public Map<String, Holding> getHoldings() {
        return holdings;
    }

    public double getTotalInvestment() {
        double total = 0;
        for (Holding h : holdings.values()) {
            total += h.quantity * h.averageBuyPrice;
        }
        return total;
    }

    public double getCurrentValue(Map<String, Stock> marketStocks) {
        double total = 0;
        for (Map.Entry<String, Holding> entry : holdings.entrySet()) {
            Stock stock = marketStocks.get(entry.getKey());
            if (stock != null) {
                total += entry.getValue().quantity * stock.getPrice();
            }
        }
        return total;
    }

    public double getProfitLoss(Map<String, Stock> marketStocks) {
        return getCurrentValue(marketStocks) - getTotalInvestment();
    }

    // ── Display ──────────────────────────────────────────────

    /** Displays colored portfolio table with per-holding P/L. */
    public void displayPortfolio(Map<String, Stock> marketStocks) {

        if (holdings.isEmpty()) {
            System.out.println(Colors.YELLOW + "\n  Your portfolio is empty. Start trading!" + Colors.RESET);
            return;
        }

        String divider = "─".repeat(72);

        System.out.println("\n" + Colors.BOLD_CYAN + "┌" + "─".repeat(70) + "┐" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "│" + Colors.RESET +
            Colors.BOLD_WHITE + centerPad("💼  MY PORTFOLIO", 70) + Colors.RESET +
            Colors.BOLD_CYAN + "│" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "└" + "─".repeat(70) + "┘" + Colors.RESET);

        System.out.printf(Colors.BOLD_WHITE +
            "  %-7s  %-8s  %-13s  %-13s  %-13s%n" + Colors.RESET,
            "Stock", "Qty", "Avg Buy", "Current", "P/L");
        System.out.println(Colors.CYAN + "  " + divider + Colors.RESET);

        double totalInvestment = 0;
        double totalCurrent    = 0;

        for (Map.Entry<String, Holding> entry : holdings.entrySet()) {
            String  symbol  = entry.getKey();
            Holding holding = entry.getValue();
            Stock   stock   = marketStocks.get(symbol);

            if (stock != null) {
                double currentPrice = stock.getPrice();
                double profitLoss   = (currentPrice - holding.averageBuyPrice) * holding.quantity;
                totalInvestment    += holding.quantity * holding.averageBuyPrice;
                totalCurrent       += holding.quantity * currentPrice;

                System.out.printf(
                    "  " + Colors.BOLD_WHITE + "%-7s" + Colors.RESET +
                    "  %-8d  " + Colors.YELLOW + "$%-12.2f" + Colors.RESET +
                    "  " + Colors.YELLOW + "$%-12.2f" + Colors.RESET +
                    "  %s%n",
                    symbol,
                    holding.quantity,
                    holding.averageBuyPrice,
                    currentPrice,
                    Colors.colorPL(profitLoss)
                );
            }
        }

        double totalPL = totalCurrent - totalInvestment;
        System.out.println(Colors.CYAN + "  " + divider + Colors.RESET);
        System.out.printf("  %-28s " + Colors.YELLOW + "$%.2f%n" + Colors.RESET,
            "Total Invested:", totalInvestment);
        System.out.printf("  %-28s " + Colors.YELLOW + "$%.2f%n" + Colors.RESET,
            "Current Value:", totalCurrent);
        System.out.printf("  %-28s %s%n",
            "Unrealized P/L:", Colors.colorPL(totalPL));
    }

    // ── Utility ──────────────────────────────────────────────

    private static String centerPad(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }

    // ── Inner class ──────────────────────────────────────────

    public static class Holding {

        private int    quantity;
        private double averageBuyPrice;

        public Holding(int quantity, double averageBuyPrice) {
            this.quantity       = quantity;
            this.averageBuyPrice = averageBuyPrice;
        }

        public int    getQuantity()      { return quantity; }
        public double getAverageBuyPrice() { return averageBuyPrice; }
    }
}

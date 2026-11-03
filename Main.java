import java.util.Scanner;

/**
 * Entry point for the Stock Trading Platform.
 *
 * Features:
 *  ✅ Colorful ANSI terminal UI
 *  ✅ User Login / Registration
 *  ✅ Buy & Sell stocks with full validation
 *  ✅ Portfolio tracking with P/L
 *  ✅ Transaction history (colored)
 *  ✅ Market price simulation
 *  ✅ ASCII price history chart
 *  ✅ CSV transaction export
 *  ✅ Multi-user file persistence
 *
 * @author Amit-Shukla07
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // ── Entry point ──────────────────────────────────────────

    public static void main(String[] args) {

        DataManager.initializeDataFolder();

        // Step 1: Authentication
        AuthManager auth = new AuthManager();
        String username  = auth.authenticate(scanner);

        // Step 2: Load user data & market
        StockMarket market = new StockMarket();
        User        user   = DataManager.loadUser(username);

        // Step 3: Main trading loop
        boolean running = true;
        while (running) {
            displayMenu(user);
            int choice = readInt("  Enter your choice: ");

            switch (choice) {

                case 1:
                    market.displayMarket();
                    break;

                case 2:
                    handleBuy(user, market);
                    break;

                case 3:
                    handleSell(user, market);
                    break;

                case 4:
                    user.getPortfolio().displayPortfolio(market.getStocks());
                    break;

                case 5:
                    user.displayTransactions();
                    break;

                case 6:
                    user.displayBalance();
                    break;

                case 7:
                    market.updateMarketPrices();
                    break;

                case 8:
                    showPerformance(user, market);
                    break;

                case 9:
                    showPriceHistory(market);
                    break;

                case 10:
                    CsvExporter.exportTransactions(
                        user.getTransactions(),
                        user.getUsername()
                    );
                    break;

                case 11:
                    DataManager.saveData(user, market);
                    running = false;
                    System.out.println(Colors.BOLD_CYAN +
                        "\n  👋  Thank you for using Stock Trading Platform!" +
                        Colors.RESET);
                    break;

                default:
                    System.out.println(Colors.RED +
                        "  ⚠  Invalid choice. Please select 1–11." + Colors.RESET);
            }
        }

        scanner.close();
    }

    // ── Menu ─────────────────────────────────────────────────

    private static void displayMenu(User user) {

        System.out.println("\n" + Colors.BOLD_CYAN +
            "  ╔══════════════════════════════════════════╗\n" +
            "  ║      📈  STOCK TRADING PLATFORM          ║\n" +
            "  ╚══════════════════════════════════════════╝" +
            Colors.RESET);

        System.out.printf(
            Colors.BOLD_WHITE + "  👤 User: " + Colors.CYAN + "%-20s" + Colors.RESET +
            Colors.BOLD_WHITE + "  💵 Balance: " + Colors.BOLD_YELLOW + "$%.2f%n" + Colors.RESET,
            user.getUsername(), user.getBalance()
        );

        System.out.println(Colors.CYAN + "  " + "─".repeat(44) + Colors.RESET);

        String[][] menu = {
            {"1",  "📊", "View Market Data"},
            {"2",  "🛒", "Buy Stock"},
            {"3",  "💰", "Sell Stock"},
            {"4",  "💼", "View My Portfolio"},
            {"5",  "📋", "Transaction History"},
            {"6",  "💵", "Account Balance"},
            {"7",  "🔄", "Simulate Market Prices"},
            {"8",  "📈", "Portfolio Performance"},
            {"9",  "📉", "View Price History Chart"},
            {"10", "📁", "Export Transactions to CSV"},
            {"11", "💾", "Save & Exit"},
        };

        for (String[] item : menu) {
            System.out.printf(
                "  " + Colors.BOLD_WHITE + "[%2s]" + Colors.RESET +
                " %s  %s%n",
                item[0], item[1], item[2]
            );
        }

        System.out.println(Colors.CYAN + "  " + "─".repeat(44) + Colors.RESET);
    }

    // ── Buy handler ──────────────────────────────────────────

    private static void handleBuy(User user, StockMarket market) {

        System.out.println("\n" + Colors.BOLD_GREEN +
            "  ═══════════  🛒  BUY STOCK  ═══════════" + Colors.RESET);

        market.displayMarket();

        System.out.print(Colors.WHITE + "\n  Enter stock symbol: " + Colors.RESET);
        String symbol = scanner.nextLine().trim().toUpperCase();

        // Validate symbol
        if (symbol.isEmpty()) {
            System.out.println(Colors.RED + "  ⚠  Symbol cannot be empty." + Colors.RESET);
            return;
        }

        Stock stock = market.getStock(symbol);
        if (stock == null) {
            System.out.println(Colors.RED +
                "  ✖  Stock '" + symbol + "' not found in market." + Colors.RESET);
            return;
        }

        System.out.printf(Colors.YELLOW +
            "  Current price of %s: $%.2f%n" + Colors.RESET,
            symbol, stock.getPrice());
        System.out.printf(Colors.CYAN +
            "  Your available balance: $%.2f%n" + Colors.RESET,
            user.getBalance());

        int quantity = readPositiveInt("  Enter quantity to buy: ");
        if (quantity <= 0) return;

        double totalCost = stock.getPrice() * quantity;

        System.out.printf(Colors.YELLOW +
            "\n  ┌─────────────────────────────┐%n" +
            "  │  Order Summary              │%n" +
            "  │  Stock    : %-16s │%n" +
            "  │  Qty      : %-16d │%n" +
            "  │  Price    : $%-15.2f │%n" +
            "  │  Total    : $%-15.2f │%n" +
            "  └─────────────────────────────┘%n" + Colors.RESET,
            symbol, quantity, stock.getPrice(), totalCost);

        if (totalCost > user.getBalance()) {
            System.out.println(Colors.BOLD_RED +
                "  ✖  Insufficient funds! You need $" + String.format("%.2f", totalCost) +
                " but have $" + String.format("%.2f", user.getBalance()) + Colors.RESET);
            return;
        }

        System.out.print(Colors.WHITE + "  Confirm purchase? (y/n): " + Colors.RESET);
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println(Colors.YELLOW + "  Order cancelled." + Colors.RESET);
            return;
        }

        boolean success = user.buyStock(stock, quantity);
        if (success) {
            System.out.println(Colors.BOLD_GREEN +
                "\n  ✔  Purchase successful!" +
                "\n     " + quantity + " share(s) of " + symbol +
                " bought at $" + String.format("%.2f", stock.getPrice()) +
                "\n     New balance: $" + String.format("%.2f", user.getBalance()) +
                Colors.RESET);
        }
    }

    // ── Sell handler ─────────────────────────────────────────

    private static void handleSell(User user, StockMarket market) {

        System.out.println("\n" + Colors.BOLD_RED +
            "  ══════════════  💰  SELL STOCK  ══════════════" + Colors.RESET);

        if (user.getPortfolio().getHoldings().isEmpty()) {
            System.out.println(Colors.YELLOW +
                "  ⚠  You have no stocks to sell." + Colors.RESET);
            return;
        }

        user.getPortfolio().displayPortfolio(market.getStocks());

        System.out.print(Colors.WHITE + "\n  Enter stock symbol to sell: " + Colors.RESET);
        String symbol = scanner.nextLine().trim().toUpperCase();

        if (symbol.isEmpty()) {
            System.out.println(Colors.RED + "  ⚠  Symbol cannot be empty." + Colors.RESET);
            return;
        }

        Stock stock = market.getStock(symbol);
        if (stock == null) {
            System.out.println(Colors.RED + "  ✖  Stock not found." + Colors.RESET);
            return;
        }

        int owned = user.getPortfolio().getQuantity(symbol);
        if (owned == 0) {
            System.out.println(Colors.RED +
                "  ✖  You do not own any shares of " + symbol + Colors.RESET);
            return;
        }

        System.out.printf(Colors.CYAN +
            "  You own %d share(s) of %s at $%.2f each%n" + Colors.RESET,
            owned, symbol, stock.getPrice());

        int quantity = readPositiveInt("  Enter quantity to sell: ");
        if (quantity <= 0) return;

        if (quantity > owned) {
            System.out.println(Colors.RED +
                "  ✖  You only own " + owned + " share(s)." + Colors.RESET);
            return;
        }

        double totalReceived = stock.getPrice() * quantity;
        double projectedPL   = user.calculateSellProfit(stock, quantity);

        System.out.printf(Colors.YELLOW +
            "\n  ┌─────────────────────────────────┐%n" +
            "  │  Sell Summary                   │%n" +
            "  │  Stock      : %-16s   │%n" +
            "  │  Qty        : %-16d   │%n" +
            "  │  Sell Price : $%-15.2f   │%n" +
            "  │  You Receive: $%-15.2f   │%n" +
            "  │  Est. P/L   : %-17s   │%n" +
            "  └─────────────────────────────────┘%n",
            symbol, quantity, stock.getPrice(), totalReceived,
            Colors.colorPL(projectedPL));

        System.out.print(Colors.WHITE + "  Confirm sell? (y/n): " + Colors.RESET);
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println(Colors.YELLOW + "  Order cancelled." + Colors.RESET);
            return;
        }

        boolean success = user.sellStock(stock, quantity);
        if (success) {
            System.out.println(Colors.BOLD_GREEN +
                "\n  ✔  Sale successful!" +
                "\n     Sold " + quantity + " share(s) of " + symbol +
                " for $" + String.format("%.2f", totalReceived) +
                "\n     Realized P/L: " + Colors.colorPL(projectedPL) +
                "\n     New balance: $" + String.format("%.2f", user.getBalance()) +
                Colors.RESET);
        }
    }

    // ── Performance view ─────────────────────────────────────

    private static void showPerformance(User user, StockMarket market) {

        double investment  = user.getPortfolio().getTotalInvestment();
        double current     = user.getPortfolio().getCurrentValue(market.getStocks());
        double unrealized  = user.getPortfolio().getProfitLoss(market.getStocks());
        double realized    = user.getRealizedProfitLoss();
        double total       = unrealized + realized;
        double returnPct   = investment == 0 ? 0 : (unrealized / investment) * 100;

        System.out.println("\n" + Colors.BOLD_CYAN + "  ┌" + "─".repeat(42) + "┐" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "  │" + Colors.RESET +
            Colors.BOLD_WHITE + centerPad("📈  PORTFOLIO PERFORMANCE", 42) + Colors.RESET +
            Colors.BOLD_CYAN + "│" + Colors.RESET);
        System.out.println(Colors.BOLD_CYAN + "  └" + "─".repeat(42) + "┘" + Colors.RESET);

        System.out.printf("  %-25s " + Colors.YELLOW + "$%.2f%n" + Colors.RESET,
            "Total Invested:", investment);
        System.out.printf("  %-25s " + Colors.YELLOW + "$%.2f%n" + Colors.RESET,
            "Current Value:", current);
        System.out.printf("  %-25s %s%n",
            "Unrealized P/L:", Colors.colorPL(unrealized));
        System.out.printf("  %-25s %s%n",
            "Realized P/L:", Colors.colorPL(realized));
        System.out.printf("  %-25s %s%n",
            "Total P/L:", Colors.colorPL(total));
        System.out.printf("  %-25s %s%.2f%%%s%n",
            "Return:",
            returnPct >= 0 ? Colors.BOLD_GREEN : Colors.BOLD_RED,
            returnPct,
            Colors.RESET);
    }

    // ── Price history chart ──────────────────────────────────

    private static void showPriceHistory(StockMarket market) {

        System.out.print(Colors.WHITE +
            "\n  Enter stock symbol (or ALL for all stocks): " + Colors.RESET);
        String input = scanner.nextLine().trim().toUpperCase();

        if (input.equals("ALL")) {
            for (Stock stock : market.getStocks().values()) {
                System.out.println();
                stock.displayPriceHistory();
            }
        } else {
            if (input.isEmpty()) {
                System.out.println(Colors.RED + "  ⚠  Symbol cannot be empty." + Colors.RESET);
                return;
            }
            market.displayPriceHistory(input);
        }
    }

    // ── Input helpers ────────────────────────────────────────

    /** Reads an integer, retries on invalid input. */
    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(Colors.BOLD_WHITE + prompt + Colors.RESET);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED +
                    "  ⚠  Please enter a valid number." + Colors.RESET);
            }
        }
    }

    /**
     * Reads a positive integer. Returns -1 and prints an error
     * if the user enters zero or a negative value.
     */
    private static int readPositiveInt(String prompt) {
        int value = readInt(prompt);
        if (value <= 0) {
            System.out.println(Colors.RED +
                "  ⚠  Quantity must be greater than zero." + Colors.RESET);
            return -1;
        }
        return value;
    }

    private static String centerPad(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }
}

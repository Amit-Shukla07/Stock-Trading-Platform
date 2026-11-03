import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Handles file-based persistence for user account, portfolio,
 * and transaction data. Each user gets their own save files.
 */
public class DataManager {

    private static final String DATA_FOLDER = "data";

    // Per-user file paths
    private static String accountFile(String username) {
        return DATA_FOLDER + File.separator + username + "_account.txt";
    }

    private static String portfolioFile(String username) {
        return DATA_FOLDER + File.separator + username + "_portfolio.txt";
    }

    private static String transactionFile(String username) {
        return DATA_FOLDER + File.separator + username + "_transactions.txt";
    }

    // ── Initialization ───────────────────────────────────────

    public static void initializeDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // ── Load ─────────────────────────────────────────────────

    /**
     * Loads user data for the given username.
     * Creates a fresh user with $100,000 if no save file exists.
     */
    public static User loadUser(String username) {

        File file = new File(accountFile(username));

        if (!file.exists()) {
            System.out.println(Colors.YELLOW +
                "  No saved data found. Starting fresh with $100,000.00" +
                Colors.RESET);
            return new User(username, 100000.00);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String savedUsername = reader.readLine();
            double balance       = Double.parseDouble(reader.readLine());
            double realizedPL    = Double.parseDouble(reader.readLine());

            User user = new User(savedUsername, balance);
            user.setRealizedProfitLoss(realizedPL);

            loadPortfolio(user);
            loadTransactions(user);

            System.out.println(Colors.BOLD_GREEN +
                "  ✔  Saved data loaded successfully." + Colors.RESET);
            return user;

        } catch (Exception e) {
            System.out.println(Colors.RED +
                "  ⚠  Could not load saved data. Starting fresh." + Colors.RESET);
            return new User(username, 100000.00);
        }
    }

    // ── Save ─────────────────────────────────────────────────

    /** Saves all user data to per-user files. */
    public static void saveData(User user, StockMarket market) {

        initializeDataFolder();

        saveAccount(user);
        savePortfolio(user);
        saveTransactions(user);

        System.out.println(Colors.BOLD_GREEN +
            "  ✔  All data saved successfully." + Colors.RESET);
    }

    private static void saveAccount(User user) {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(accountFile(user.getUsername())))) {

            writer.println(user.getUsername());
            writer.println(user.getBalance());
            writer.println(user.getRealizedProfitLoss());

        } catch (IOException e) {
            System.out.println(Colors.RED +
                "  Error saving account: " + e.getMessage() + Colors.RESET);
        }
    }

    private static void savePortfolio(User user) {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(portfolioFile(user.getUsername())))) {

            for (Map.Entry<String, Portfolio.Holding> entry :
                    user.getPortfolio().getHoldings().entrySet()) {

                writer.println(
                    entry.getKey() + "," +
                    entry.getValue().getQuantity() + "," +
                    entry.getValue().getAverageBuyPrice()
                );
            }
        } catch (IOException e) {
            System.out.println(Colors.RED +
                "  Error saving portfolio: " + e.getMessage() + Colors.RESET);
        }
    }

    private static void saveTransactions(User user) {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(transactionFile(user.getUsername())))) {

            for (Transaction t : user.getTransactions()) {
                writer.println(
                    t.getType()        + "," +
                    t.getStockSymbol() + "," +
                    t.getQuantity()    + "," +
                    t.getPrice()       + "," +
                    t.getProfitLoss()  + "," +
                    t.getDateTime()
                );
            }
        } catch (IOException e) {
            System.out.println(Colors.RED +
                "  Error saving transactions: " + e.getMessage() + Colors.RESET);
        }
    }

    // ── Private loaders ──────────────────────────────────────

    private static void loadPortfolio(User user) {
        File file = new File(portfolioFile(user.getUsername()));
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data    = line.split(",");
                String   symbol  = data[0];
                int      qty     = Integer.parseInt(data[1]);
                double   avgPrice = Double.parseDouble(data[2]);
                // Restore directly — no need to loop qty times
                user.getPortfolio().buyStock(symbol, qty, avgPrice);
            }
        } catch (Exception e) {
            System.out.println(Colors.RED + "  Error loading portfolio." + Colors.RESET);
        }
    }

    private static void loadTransactions(User user) {
        File file = new File(transactionFile(user.getUsername()));
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[]      data      = line.split(",");
                String        type      = data[0];
                String        symbol    = data[1];
                int           qty       = Integer.parseInt(data[2]);
                double        price     = Double.parseDouble(data[3]);
                double        pl        = Double.parseDouble(data[4]);
                LocalDateTime dateTime  = LocalDateTime.parse(data[5]);

                user.getTransactions().add(
                    new Transaction(type, symbol, qty, price, pl, dateTime)
                );
            }
        } catch (Exception e) {
            System.out.println(Colors.RED + "  Error loading transactions." + Colors.RESET);
        }
    }
}

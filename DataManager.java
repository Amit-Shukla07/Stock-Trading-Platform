import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;

public class DataManager {

    private static final String DATA_FOLDER = "data";
    private static final String ACCOUNT_FILE =
            DATA_FOLDER + File.separator + "account.txt";

    private static final String PORTFOLIO_FILE =
            DATA_FOLDER + File.separator + "portfolio.txt";

    private static final String TRANSACTION_FILE =
            DATA_FOLDER + File.separator + "transactions.txt";

    public static void initializeDataFolder() {

        File folder = new File(DATA_FOLDER);

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void saveData(
            User user,
            StockMarket market) {

        initializeDataFolder();

        saveAccount(user);

        savePortfolio(user);

        saveTransactions(user);

        System.out.println(
                "Application data saved successfully."
        );
    }

    private static void saveAccount(User user) {

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(ACCOUNT_FILE)
                        )
        ) {

            writer.println(user.getUsername());
            writer.println(user.getBalance());
            writer.println(user.getRealizedProfitLoss());

        } catch (IOException e) {

            System.out.println(
                    "Error saving account data: "
                            + e.getMessage()
            );
        }
    }

    private static void savePortfolio(User user) {

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(PORTFOLIO_FILE)
                        )
        ) {

            for (
                    Map.Entry<String, Portfolio.Holding> entry
                    : user.getPortfolio()
                    .getHoldings()
                    .entrySet()
            ) {

                writer.println(
                        entry.getKey()
                                + ","
                                + entry.getValue().getQuantity()
                                + ","
                                + entry.getValue().getAverageBuyPrice()
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error saving portfolio data: "
                            + e.getMessage()
            );
        }
    }

    private static void saveTransactions(User user) {

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(TRANSACTION_FILE)
                        )
        ) {

            for (
                    Transaction transaction
                    : user.getTransactions()
            ) {

                writer.println(
                        transaction.getType()
                                + ","
                                + transaction.getStockSymbol()
                                + ","
                                + transaction.getQuantity()
                                + ","
                                + transaction.getPrice()
                                + ","
                                + transaction.getProfitLoss()
                                + ","
                                + transaction.getDateTime()
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error saving transaction data: "
                            + e.getMessage()
            );
        }
    }

    public static User loadUser() {

        File accountFile =
                new File(ACCOUNT_FILE);

        if (!accountFile.exists()) {

            return new User(
                    "Amit-Shukla07",
                    100000.00
            );
        }

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(accountFile)
                        )
        ) {

            String username = reader.readLine();

            double balance =
                    Double.parseDouble(
                            reader.readLine()
                    );

            double realizedProfitLoss =
                    Double.parseDouble(
                            reader.readLine()
                    );

            User user =
                    new User(
                            username,
                            balance
                    );
                    user.setRealizedProfitLoss(realizedProfitLoss);

            loadPortfolio(user);
            loadTransactions(user);

            return user;

        } catch (Exception e) {

            System.out.println(
                    "Saved data could not be loaded."
            );

            return new User(
                    "Amit-Shukla07",
                    100000.00
            );
        }
    }

    private static void loadPortfolio(User user) {

        File file =
                new File(PORTFOLIO_FILE);

        if (!file.exists()) {
            return;
        }

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file)
                        )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data =
                        line.split(",");

                String symbol = data[0];

                int quantity =
                        Integer.parseInt(data[1]);

                double averagePrice =
                        Double.parseDouble(data[2]);

                for (int i = 0; i < quantity; i++) {

                    user.getPortfolio().buyStock(
                            symbol,
                            1,
                            averagePrice
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error loading portfolio data."
            );
        }
    }

    private static void loadTransactions(User user) {

        File file =
                new File(TRANSACTION_FILE);

        if (!file.exists()) {
            return;
        }

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file)
                        )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data =
                        line.split(",");

                String type = data[0];
                String symbol = data[1];

                int quantity =
                        Integer.parseInt(data[2]);

                double price =
                        Double.parseDouble(data[3]);

                double profitLoss =
                        Double.parseDouble(data[4]);

                LocalDateTime dateTime =
                        LocalDateTime.parse(data[5]);

                user.getTransactions().add(
                        new Transaction(
                                type,
                                symbol,
                                quantity,
                                price,
                                profitLoss,
                                dateTime
                        )
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Error loading transaction data."
            );
        }
    }
}
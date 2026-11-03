import java.util.Scanner;

public class Main {

    private static Scanner scanner =
            new Scanner(System.in);

    public static void main(String[] args) {

        DataManager.initializeDataFolder();

        StockMarket market =
                new StockMarket();

        User user =
                DataManager.loadUser();

        boolean running = true;

        System.out.println();
        System.out.println(
                "╔══════════════════════════════════════════╗"
        );

        System.out.println(
                "║       STOCK TRADING PLATFORM             ║"
        );

        System.out.println(
                "╚══════════════════════════════════════════╝"
        );

        System.out.println(
                "\nWelcome, "
                        + user.getUsername()
                        + "!"
        );

        while (running) {

            displayMenu(user);

            int choice =
                    readInteger(
                            "Enter your choice: "
                    );

            switch (choice) {

                case 1:
                    market.displayMarket();
                    break;

                case 2:
                    buyStock(user, market);
                    break;

                case 3:
                    sellStock(user, market);
                    break;

                case 4:
                    user.getPortfolio()
                            .displayPortfolio(
                                    market.getStocks()
                            );
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
                    showPerformance(
                            user,
                            market
                    );
                    break;

                case 9:

                    DataManager.saveData(
                            user,
                            market
                    );

                    running = false;

                    System.out.println(
                            "\nThank you for using "
                                    + "Stock Trading Platform!"
                    );

                    break;

                default:

                    System.out.println(
                            "\nInvalid choice. "
                                    + "Please select 1-9."
                    );
            }
        }

        scanner.close();
    }

    private static void displayMenu(User user) {

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "              MAIN MENU"
        );

        System.out.println(
                "=========================================="
        );

        System.out.printf(
                "Available Balance: $%.2f%n",
                user.getBalance()
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "1. View Market Data"
        );

        System.out.println(
                "2. Buy Stock"
        );

        System.out.println(
                "3. Sell Stock"
        );

        System.out.println(
                "4. View Portfolio"
        );

        System.out.println(
                "5. View Transactions"
        );

        System.out.println(
                "6. View Account Balance"
        );

        System.out.println(
                "7. Update Market Prices"
        );

        System.out.println(
                "8. View Portfolio Performance"
        );

        System.out.println(
                "9. Save & Exit"
        );

        System.out.println(
                "=========================================="
        );
    }

    private static void buyStock(
            User user,
            StockMarket market) {

        System.out.println(
                "\n========== BUY STOCK =========="
        );

        String symbol =
                readString(
                        "Enter stock symbol: "
                ).toUpperCase();

        Stock stock =
                market.getStock(symbol);

        if (stock == null) {

            System.out.println(
                    "Stock not found."
            );

            return;
        }

        System.out.printf(
                "Current price of %s: $%.2f%n",
                symbol,
                stock.getPrice()
        );

        int quantity =
                readInteger(
                        "Enter quantity: "
                );

        if (quantity <= 0) {

            System.out.println(
                    "Quantity must be greater than zero."
            );

            return;
        }

        double totalCost =
                stock.getPrice() * quantity;

        System.out.printf(
                "Total Cost: $%.2f%n",
                totalCost
        );

        if (totalCost > user.getBalance()) {

            System.out.println(
                    "Insufficient balance."
            );

            return;
        }

        boolean success =
                user.buyStock(
                        stock,
                        quantity
                );

        if (success) {

            System.out.println(
                    "\nPurchase successful!"
            );

            System.out.println(
                    "Stock: " + symbol
            );

            System.out.println(
                    "Quantity: " + quantity
            );

            System.out.printf(
                    "Price: $%.2f%n",
                    stock.getPrice()
            );

            System.out.printf(
                    "Total Paid: $%.2f%n",
                    totalCost
            );
        }
    }

    private static void sellStock(
            User user,
            StockMarket market) {

        System.out.println(
                "\n========== SELL STOCK =========="
        );

        String symbol =
                readString(
                        "Enter stock symbol: "
                ).toUpperCase();

        Stock stock =
                market.getStock(symbol);

        if (stock == null) {

            System.out.println(
                    "Stock not found."
            );

            return;
        }

        int owned =
                user.getPortfolio()
                        .getQuantity(symbol);

        if (owned == 0) {

            System.out.println(
                    "You do not own this stock."
            );

            return;
        }

        System.out.println(
                "You currently own "
                        + owned
                        + " shares."
        );

        System.out.printf(
                "Current price: $%.2f%n",
                stock.getPrice()
        );

        int quantity =
                readInteger(
                        "Enter quantity to sell: "
                );

        if (quantity <= 0) {

            System.out.println(
                    "Quantity must be greater than zero."
            );

            return;
        }

        if (quantity > owned) {

            System.out.println(
                    "You do not own enough shares."
            );

            return;
        }

        double totalReceived =
                stock.getPrice() * quantity;

        double profitLoss =
                user.calculateSellProfit(
                        stock,
                        quantity
                );

        boolean success =
                user.sellStock(
                        stock,
                        quantity
                );

        if (success) {

            System.out.println(
                    "\nSale successful!"
            );

            System.out.println(
                    "Stock: " + symbol
            );

            System.out.println(
                    "Quantity: " + quantity
            );

            System.out.printf(
                    "Total Received: $%.2f%n",
                    totalReceived
            );

            System.out.printf(
                    "Realized P/L: $%.2f%n",
                    profitLoss
            );
        }
    }

    private static void showPerformance(
            User user,
            StockMarket market) {

        double investment =
                user.getPortfolio()
                        .getTotalInvestment();

        double currentValue =
                user.getPortfolio()
                        .getCurrentValue(
                                market.getStocks()
                        );

        double unrealizedPL =
                user.getPortfolio()
                        .getProfitLoss(
                                market.getStocks()
                        );

        double realizedPL =
                user.getRealizedProfitLoss();

        double totalPL =
                unrealizedPL + realizedPL;

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "          PORTFOLIO PERFORMANCE"
        );

        System.out.println(
                "=========================================="
        );

        System.out.printf(
                "Total Investment : $%.2f%n",
                investment
        );

        System.out.printf(
                "Current Value    : $%.2f%n",
                currentValue
        );

        System.out.printf(
                "Unrealized P/L   : $%.2f%n",
                unrealizedPL
        );

        System.out.printf(
                "Realized P/L     : $%.2f%n",
                realizedPL
        );

        System.out.printf(
                "Total P/L        : $%.2f%n",
                totalPL
        );
    }

    private static int readInteger(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        scanner.nextLine()
                                .trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. "
                                + "Please enter a number."
                );
            }
        }
    }

    private static String readString(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }
}
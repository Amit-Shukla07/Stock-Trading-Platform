import java.util.ArrayList;

public class User {

    private String username;
    private double balance;
    private Portfolio portfolio;
    private ArrayList<Transaction> transactions;
    private double realizedProfitLoss;

    public User(String username, double balance) {

        this.username = username;
        this.balance = balance;

        portfolio = new Portfolio();
        transactions = new ArrayList<>();

        realizedProfitLoss = 0;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public double getRealizedProfitLoss() {
        return realizedProfitLoss;
    }

    public boolean buyStock(Stock stock, int quantity) {

        if (quantity <= 0) {
            return false;
        }

        double totalCost = stock.getPrice() * quantity;

        if (totalCost > balance) {
            return false;
        }

        balance -= totalCost;

        portfolio.buyStock(
                stock.getSymbol(),
                quantity,
                stock.getPrice()
        );

        Transaction transaction = new Transaction(
                "BUY",
                stock.getSymbol(),
                quantity,
                stock.getPrice(),
                0
        );

        transactions.add(transaction);

        return true;
    }

    public double calculateSellProfit(
            Stock stock,
            int quantity) {

        double averagePrice =
                portfolio.getAverageBuyPrice(
                        stock.getSymbol()
                );

        return (stock.getPrice() - averagePrice) * quantity;
    }

    public boolean sellStock(Stock stock, int quantity) {

        if (quantity <= 0) {
            return false;
        }

        if (!portfolio.ownsStock(stock.getSymbol())) {
            return false;
        }

        if (portfolio.getQuantity(stock.getSymbol()) < quantity) {
            return false;
        }

        double totalReceived =
                stock.getPrice() * quantity;

        double profitLoss =
                calculateSellProfit(stock, quantity);

        portfolio.sellStock(
                stock.getSymbol(),
                quantity
        );

        balance += totalReceived;

        realizedProfitLoss += profitLoss;

        Transaction transaction = new Transaction(
                "SELL",
                stock.getSymbol(),
                quantity,
                stock.getPrice(),
                profitLoss
        );

        transactions.add(transaction);

        return true;
    }

    public void displayBalance() {

        System.out.println("\n========================================");
        System.out.println("             ACCOUNT BALANCE");
        System.out.println("========================================");

        System.out.printf(
                "Available Cash : $%.2f%n",
                balance
        );

        System.out.printf(
                "Realized P/L   : $%.2f%n",
                realizedProfitLoss
        );
    }

    public void displayTransactions() {

        if (transactions.isEmpty()) {

            System.out.println(
                    "\nNo transactions found."
            );

            return;
        }

        System.out.println(
                "\n================================================================================"
        );

        System.out.println(
                "                         TRANSACTION HISTORY"
        );

        System.out.println(
                "================================================================================"
        );

        System.out.printf(
                "%-6s %-8s %-8s %-11s %-13s %-13s %s%n",
                "Type",
                "Stock",
                "Qty",
                "Price",
                "Total",
                "P/L",
                "Date & Time"
        );

        System.out.println(
                "--------------------------------------------------------------------------------"
        );

        for (Transaction transaction : transactions) {
            transaction.displayTransaction();
        }
    }
    public void setRealizedProfitLoss(double realizedProfitLoss) {
    this.realizedProfitLoss = realizedProfitLoss;
}
}
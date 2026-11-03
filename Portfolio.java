import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private Map<String, Holding> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    public void buyStock(String symbol, int quantity, double price) {

        if (holdings.containsKey(symbol)) {

            Holding holding = holdings.get(symbol);

            double totalOldInvestment =
                    holding.quantity * holding.averageBuyPrice;

            double newInvestment = quantity * price;

            int newQuantity = holding.quantity + quantity;

            holding.averageBuyPrice =
                    (totalOldInvestment + newInvestment) / newQuantity;

            holding.quantity = newQuantity;

        } else {

            holdings.put(
                    symbol,
                    new Holding(quantity, price)
            );
        }
    }

    public boolean sellStock(String symbol, int quantity) {

        if (!holdings.containsKey(symbol)) {
            return false;
        }

        Holding holding = holdings.get(symbol);

        if (quantity > holding.quantity) {
            return false;
        }

        holding.quantity -= quantity;

        if (holding.quantity == 0) {
            holdings.remove(symbol);
        }

        return true;
    }

    public boolean ownsStock(String symbol) {
        return holdings.containsKey(symbol);
    }

    public int getQuantity(String symbol) {

        if (!holdings.containsKey(symbol)) {
            return 0;
        }

        return holdings.get(symbol).quantity;
    }

    public double getAverageBuyPrice(String symbol) {

        if (!holdings.containsKey(symbol)) {
            return 0;
        }

        return holdings.get(symbol).averageBuyPrice;
    }

    public Map<String, Holding> getHoldings() {
        return holdings;
    }

    public double getTotalInvestment() {

        double total = 0;

        for (Holding holding : holdings.values()) {
            total += holding.quantity * holding.averageBuyPrice;
        }

        return total;
    }

    public double getCurrentValue(Map<String, Stock> marketStocks) {

        double total = 0;

        for (Map.Entry<String, Holding> entry : holdings.entrySet()) {

            String symbol = entry.getKey();
            Holding holding = entry.getValue();

            Stock stock = marketStocks.get(symbol);

            if (stock != null) {
                total += holding.quantity * stock.getPrice();
            }
        }

        return total;
    }

    public double getProfitLoss(Map<String, Stock> marketStocks) {

        return getCurrentValue(marketStocks) - getTotalInvestment();
    }

    public void displayPortfolio(Map<String, Stock> marketStocks) {

        if (holdings.isEmpty()) {

            System.out.println("\nYour portfolio is empty.");
            return;
        }

        System.out.println("\n==============================================================");
        System.out.println("                      MY PORTFOLIO");
        System.out.println("==============================================================");

        System.out.printf(
                "%-10s %-10s %-15s %-15s %-15s%n",
                "Stock",
                "Quantity",
                "Avg Price",
                "Current",
                "P/L"
        );

        System.out.println("--------------------------------------------------------------");

        for (Map.Entry<String, Holding> entry : holdings.entrySet()) {

            String symbol = entry.getKey();
            Holding holding = entry.getValue();

            Stock stock = marketStocks.get(symbol);

            if (stock != null) {

                double currentPrice = stock.getPrice();

                double profitLoss =
                        (currentPrice - holding.averageBuyPrice)
                                * holding.quantity;

                System.out.printf(
                        "%-10s %-10d $%-14.2f $%-14.2f $%-14.2f%n",
                        symbol,
                        holding.quantity,
                        holding.averageBuyPrice,
                        currentPrice,
                        profitLoss
                );
            }
        }

        System.out.println("--------------------------------------------------------------");

        double investment = getTotalInvestment();
        double currentValue = getCurrentValue(marketStocks);
        double profitLoss = getProfitLoss(marketStocks);

        System.out.printf("Total Investment : $%.2f%n", investment);
        System.out.printf("Current Value    : $%.2f%n", currentValue);
        System.out.printf("Unrealized P/L   : $%.2f%n", profitLoss);
    }

    public static class Holding {

        private int quantity;
        private double averageBuyPrice;

        public Holding(int quantity, double averageBuyPrice) {
            this.quantity = quantity;
            this.averageBuyPrice = averageBuyPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getAverageBuyPrice() {
            return averageBuyPrice;
        }
    }
}
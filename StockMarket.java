import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockMarket {

    private HashMap<String, Stock> stocks;
    private Random random;

    public StockMarket() {

        stocks = new HashMap<>();
        random = new Random();

        initializeStocks();
    }

    private void initializeStocks() {

        addStock(
                new Stock(
                        "AAPL",
                        "Apple Inc.",
                        225.00
                )
        );

        addStock(
                new Stock(
                        "GOOGL",
                        "Alphabet Inc.",
                        250.00
                )
        );

        addStock(
                new Stock(
                        "MSFT",
                        "Microsoft Corp.",
                        500.00
                )
        );

        addStock(
                new Stock(
                        "AMZN",
                        "Amazon.com Inc.",
                        230.00
                )
        );

        addStock(
                new Stock(
                        "TSLA",
                        "Tesla Inc.",
                        450.00
                )
        );

        addStock(
                new Stock(
                        "META",
                        "Meta Platforms",
                        600.00
                )
        );

        addStock(
                new Stock(
                        "NFLX",
                        "Netflix Inc.",
                        700.00
                )
        );
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public Stock getStock(String symbol) {

        return stocks.get(
                symbol.toUpperCase()
        );
    }

    public Map<String, Stock> getStocks() {
        return stocks;
    }

    public void displayMarket() {

        System.out.println(
                "\n================================================"
        );

        System.out.println(
                "                 MARKET DATA"
        );

        System.out.println(
                "================================================"
        );

        System.out.printf(
                "%-10s %-25s %s%n",
                "Symbol",
                "Company",
                "Price"
        );

        System.out.println(
                "------------------------------------------------"
        );

        for (Stock stock : stocks.values()) {
            stock.displayStock();
        }
    }

    public void updateMarketPrices() {

        System.out.println(
                "\nUpdating simulated market prices..."
        );

        for (Stock stock : stocks.values()) {

            double currentPrice =
                    stock.getPrice();

            double change =
                    (random.nextDouble() * 0.10) - 0.05;

            double newPrice =
                    currentPrice + (currentPrice * change);

            if (newPrice < 1) {
                newPrice = 1;
            }

            stock.setPrice(newPrice);
        }

        System.out.println(
                "Market prices updated successfully."
        );
    }
}
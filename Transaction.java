import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private String type;
    private String stockSymbol;
    private int quantity;
    private double price;
    private double totalAmount;
    private double profitLoss;
    private LocalDateTime dateTime;

    public Transaction(
            String type,
            String stockSymbol,
            int quantity,
            double price,
            double profitLoss) {

        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = quantity * price;
        this.profitLoss = profitLoss;
        this.dateTime = LocalDateTime.now();
    }

    public Transaction(
            String type,
            String stockSymbol,
            int quantity,
            double price,
            double profitLoss,
            LocalDateTime dateTime) {

        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = quantity * price;
        this.profitLoss = profitLoss;
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void displayTransaction() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.printf(
                "%-6s %-8s %-8d $%-10.2f $%-12.2f $%-12.2f %s%n",
                type,
                stockSymbol,
                quantity,
                price,
                totalAmount,
                profitLoss,
                dateTime.format(formatter)
        );
    }
}
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exports transaction history to a CSV file.
 * The file is saved in the data/ folder with a timestamp in its name.
 */
public class CsvExporter {

    private static final String EXPORT_FOLDER = "data";
    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Exports the given list of transactions to a CSV file.
     * Returns the path of the created file, or null on failure.
     */
    public static String exportTransactions(List<Transaction> transactions, String username) {

        if (transactions.isEmpty()) {
            System.out.println(Colors.YELLOW +
                "  ⚠  No transactions to export." + Colors.RESET);
            return null;
        }

        new File(EXPORT_FOLDER).mkdirs();

        String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP);
        String fileName  = EXPORT_FOLDER + File.separator +
                           "transactions_" + username + "_" + timestamp + ".csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            // CSV header
            writer.println("Type,Stock,Quantity,Price,Total Amount,P/L,Date & Time");

            // CSV rows
            for (Transaction t : transactions) {
                writer.println(t.toCsvLine());
            }

            System.out.println(Colors.BOLD_GREEN +
                "  ✔  Exported " + transactions.size() +
                " transaction(s) to:\n     " + fileName + Colors.RESET);

            return fileName;

        } catch (IOException e) {
            System.out.println(Colors.RED +
                "  ✖  Export failed: " + e.getMessage() + Colors.RESET);
            return null;
        }
    }
}


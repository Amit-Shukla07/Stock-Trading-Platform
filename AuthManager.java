import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple file-based authentication system.
 * Stores username + hashed password in data/users.txt.
 * Supports login and new user registration.
 */
public class AuthManager {

    private static final String USERS_FILE = "data" + File.separator + "users.txt";

    // In-memory store: username -> hashed password
    private Map<String, String> users = new HashMap<>();

    public AuthManager() {
        loadUsers();
    }

    // ── Public API ───────────────────────────────────────────

    /**
     * Shows login/register screen and returns the authenticated username.
     * Loops until valid credentials are entered.
     */
    public String authenticate(Scanner scanner) {

        printBanner();

        while (true) {
            System.out.println(Colors.BOLD_CYAN + "\n  What would you like to do?" + Colors.RESET);
            System.out.println(Colors.WHITE + "  1. Login" + Colors.RESET);
            System.out.println(Colors.WHITE + "  2. Register new account" + Colors.RESET);
            System.out.println(Colors.WHITE + "  3. Exit" + Colors.RESET);
            System.out.print(Colors.BOLD_WHITE + "\n  Enter choice: " + Colors.RESET);

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    String loggedIn = doLogin(scanner);
                    if (loggedIn != null) return loggedIn;
                    break;

                case "2":
                    String registered = doRegister(scanner);
                    if (registered != null) return registered;
                    break;

                case "3":
                    System.out.println(Colors.YELLOW + "\n  Goodbye! 👋" + Colors.RESET);
                    System.exit(0);
                    break;

                default:
                    System.out.println(Colors.RED + "  ⚠  Invalid choice. Try again." + Colors.RESET);
            }
        }
    }

    // ── Private helpers ──────────────────────────────────────

    private String doLogin(Scanner scanner) {

        System.out.println(Colors.BOLD_CYAN + "\n  ── LOGIN ──" + Colors.RESET);
        System.out.print(Colors.WHITE + "  Username : " + Colors.RESET);
        String username = scanner.nextLine().trim();

        System.out.print(Colors.WHITE + "  Password : " + Colors.RESET);
        String password = scanner.nextLine().trim();

        if (!users.containsKey(username)) {
            System.out.println(Colors.BOLD_RED +
                "  ✖  Username not found. Please register first." + Colors.RESET);
            return null;
        }

        if (!users.get(username).equals(hashPassword(password))) {
            System.out.println(Colors.BOLD_RED +
                "  ✖  Incorrect password. Please try again." + Colors.RESET);
            return null;
        }

        System.out.println(Colors.BOLD_GREEN +
            "  ✔  Login successful! Welcome back, " + username + "!" + Colors.RESET);
        return username;
    }

    private String doRegister(Scanner scanner) {

        System.out.println(Colors.BOLD_CYAN + "\n  ── REGISTER ──" + Colors.RESET);
        System.out.print(Colors.WHITE + "  Choose username : " + Colors.RESET);
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println(Colors.RED + "  ⚠  Username cannot be empty." + Colors.RESET);
            return null;
        }

        if (users.containsKey(username)) {
            System.out.println(Colors.RED +
                "  ⚠  Username already taken. Please choose another." + Colors.RESET);
            return null;
        }

        System.out.print(Colors.WHITE + "  Choose password : " + Colors.RESET);
        String password = scanner.nextLine().trim();

        if (password.length() < 4) {
            System.out.println(Colors.RED +
                "  ⚠  Password must be at least 4 characters." + Colors.RESET);
            return null;
        }

        System.out.print(Colors.WHITE + "  Confirm password: " + Colors.RESET);
        String confirm = scanner.nextLine().trim();

        if (!password.equals(confirm)) {
            System.out.println(Colors.RED + "  ⚠  Passwords do not match." + Colors.RESET);
            return null;
        }

        users.put(username, hashPassword(password));
        saveUsers();

        System.out.println(Colors.BOLD_GREEN +
            "  ✔  Account created! Welcome, " + username + "! Starting balance: $100,000.00" +
            Colors.RESET);
        return username;
    }

    /** Simple hash: XOR folding for demonstration (not production crypto). */
    private String hashPassword(String password) {
        int hash = 0;
        for (char c : password.toCharArray()) {
            hash = (hash * 31 + c) & 0xFFFFFF;
        }
        return String.format("%06X", hash);
    }

    /** Loads username:hashedPassword pairs from the users file. */
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    users.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println(Colors.RED + "  Warning: could not load user accounts." + Colors.RESET);
        }
    }

    /** Saves all user accounts back to the users file. */
    private void saveUsers() {
        new File("data").mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println(Colors.RED + "  Warning: could not save user accounts." + Colors.RESET);
        }
    }

    private void printBanner() {
        System.out.println("\n" + Colors.BOLD_CYAN +
            "  ╔══════════════════════════════════════════╗\n" +
            "  ║     📈  STOCK TRADING PLATFORM           ║\n" +
            "  ║         Powered by Amit-Shukla07         ║\n" +
            "  ╚══════════════════════════════════════════╝" +
            Colors.RESET);
    }
}


/**
 * ANSI color codes for colorful terminal output.
 * Adds visual polish and makes the app look professional.
 */
public class Colors {

    // Text colors
    public static final String RESET   = "\033[0m";
    public static final String RED     = "\033[0;31m";
    public static final String GREEN   = "\033[0;32m";
    public static final String YELLOW  = "\033[0;33m";
    public static final String BLUE    = "\033[0;34m";
    public static final String CYAN    = "\033[0;36m";
    public static final String WHITE   = "\033[0;37m";

    // Bold variants
    public static final String BOLD_RED    = "\033[1;31m";
    public static final String BOLD_GREEN  = "\033[1;32m";
    public static final String BOLD_YELLOW = "\033[1;33m";
    public static final String BOLD_BLUE   = "\033[1;34m";
    public static final String BOLD_CYAN   = "\033[1;36m";
    public static final String BOLD_WHITE  = "\033[1;37m";

    /**
     * Returns the value colored green if positive, red if negative,
     * yellow if zero — perfect for P/L values.
     */
    public static String colorPL(double value) {
        String formatted = String.format("$%.2f", value);
        if (value > 0)  return BOLD_GREEN + "+" + formatted + RESET;
        if (value < 0)  return BOLD_RED   + formatted + RESET;
        return YELLOW + formatted + RESET;
    }

    /**
     * Returns the value colored green if positive, red if negative.
     * Used for price changes.
     */
    public static String colorChange(double change) {
        String arrow = change >= 0 ? "▲" : "▼";
        String formatted = String.format("%.2f%%", Math.abs(change));
        if (change > 0) return BOLD_GREEN + arrow + " " + formatted + RESET;
        if (change < 0) return BOLD_RED   + arrow + " " + formatted + RESET;
        return YELLOW + "─ 0.00%" + RESET;
    }
}


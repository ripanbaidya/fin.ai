package ai.fin.export;

public final class CsvSanitizer {

    private static final String FORMULA_TRIGGERS = "=+-@\t\r";

    private CsvSanitizer() {
    }

    /**
     * Prefixes risky text so spreadsheet apps don't execute it as a formula.
     */
    public static Object sanitize(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString();
        if (!text.isEmpty() && FORMULA_TRIGGERS.indexOf(text.charAt(0)) >= 0) {
            return "'" + text;
        }
        return text;
    }
}
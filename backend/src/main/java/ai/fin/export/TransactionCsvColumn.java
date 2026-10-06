package ai.fin.export;

import ai.fin.entities.Transaction;

import java.util.Arrays;
import java.util.function.Function;

public enum TransactionCsvColumn {

    ID("ID", Transaction::getId),
    DATE("Date", Transaction::getDate),
    TYPE("Type", Transaction::getType),
    AMOUNT("Amount", Transaction::getAmount),
    CATEGORY("Category", t -> CsvSanitizer.sanitize(t.getCategory())),
    PAYMENT_MODE("Payment Mode", Transaction::getPaymentMode),
    NOTE("Note", t -> CsvSanitizer.sanitize(t.getNote())),
    CREATED_AT("Created At", Transaction::getCreatedAt);

    private final String header;
    private final Function<Transaction, ?> valueExtractor;

    TransactionCsvColumn(String header, Function<Transaction, ?> valueExtractor) {
        this.header = header;
        this.valueExtractor = valueExtractor;
    }

    public String header() {
        return header;
    }

    public Object valueOf(Transaction transaction) {
        return valueExtractor.apply(transaction);
    }

    public static String[] headers() {
        return Arrays.stream(values()).map(TransactionCsvColumn::header).toArray(String[]::new);
    }
}
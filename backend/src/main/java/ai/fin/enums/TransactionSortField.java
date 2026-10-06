package ai.fin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionSortField {
    DATE("date"),
    AMOUNT("amount"),
    TYPE("type"),
    CREATED_AT("createdAt");

    private final String property;
}
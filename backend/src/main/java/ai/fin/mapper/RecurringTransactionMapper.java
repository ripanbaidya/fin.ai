package ai.fin.mapper;

import ai.fin.dto.recurring.RecurringTransactionResponse;
import ai.fin.entities.RecurringTransaction;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class RecurringTransactionMapper {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RecurringTransactionMapper() {
    }

    public static RecurringTransactionResponse toResponse(RecurringTransaction r) {
        return RecurringTransactionResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .amount(r.getAmount())
                .type(r.getType())
                .frequency(r.getFrequency())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .nextExecutionDate(r.getNextExecutionDate())
                .isActive(r.isActive())
                .note(r.getNote())
                .categoryName(r.getCategory() != null ? r.getCategory().getName() : null)
                .paymentModeName(r.getPaymentMode() != null ? r.getPaymentMode().getName() : null)
                .createdAt(FORMATTER.format(r.getCreatedAt().atZone(ZoneId.systemDefault())))
                .build();
    }
}
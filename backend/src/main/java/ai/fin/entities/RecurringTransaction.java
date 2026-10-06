package ai.fin.entities;

import ai.fin.enums.RecurringFrequency;
import ai.fin.enums.TxnType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "recurring_transactions",
        indexes = {
                @Index(name = "idx_recurring_scheduler", columnList = "is_active, next_execution_date"),
                @Index(name = "idx_recurring_user_active", columnList = "user_id, is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RecurringTransaction extends BaseEntity {

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private TxnType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", length = 10, nullable = false)
    private RecurringFrequency frequency;

    /**
     * The date this recurring rule starts.
     * The first transaction is created on this date and this must be today or a future date.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * The date this recurring rule stops (inclusive). If null, the rule runs indefinitely.
     * Must be after or equal to {@link #startDate}.
     */
    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    /**
     * The next date the scheduler will execute this rule. Updated after each execution.
     * Managed internally — not exposed in create/update request DTOs.
     */
    @Column(name = "next_execution_date", nullable = false)
    private LocalDate nextExecutionDate;


    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "note", length = 255)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_mode_id")
    private PaymentMode paymentMode;
}
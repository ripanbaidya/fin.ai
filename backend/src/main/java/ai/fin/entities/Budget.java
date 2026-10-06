package ai.fin.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(
        name = "budgets",
        indexes = {
                @Index(name = "idx_budget_user_month", columnList = "user_id, month")
        },
        uniqueConstraints = {
                // One budget per user per category per month
                @UniqueConstraint(
                        name = "uq_budget_user_category_month",
                        columnNames = {"user_id", "category_id", "month"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Budget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Stores the month as "yyyy-MM" (e.g., "2025-07") in PostgreSQL.
    // YearMonth is not natively supported by JPA/Hibernate, so YearMonthConverter
    // converts it to/from its ISO-8601 string representation.
    @Column(name = "month", nullable = false, length = 7)
    private YearMonth month;

    @Column(name = "limit_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal limitAmount;

    // Alert when spending crosses this % of limit (e.g. 80)
    @Column(name = "alert_threshold", nullable = false)
    private int alertThreshold = 80;
}
package ai.fin.entities;

import ai.fin.enums.TxnType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_user_id", columnList = "user_id"),
                @Index(name = "idx_transactions_user_date", columnList = "user_id, date"),
                @Index(name = "idx_transactions_category_id", columnList = "category_id"),
                @Index(name = "idx_transactions_payment_mode_id", columnList = "payment_mode_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private TxnType type;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Optional user note
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    // ID of the corresponding vector document in PgVector.
    // Nullable because it is set asynchronously after the transaction is saved.
    // Used to update or delete the vector when the transaction changes.
    @Column(name = "embedding_id", nullable = true, length = 255)
    private String embeddingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_mode_id", nullable = true)
    private PaymentMode paymentMode;
}
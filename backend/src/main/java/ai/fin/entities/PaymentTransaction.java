package ai.fin.entities;

import ai.fin.enums.PaymentProvider;
import ai.fin.enums.PaymentStatus;
import ai.fin.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "payment_transactions",
        indexes = {
                @Index(name = "idx_payment_transactions_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 50)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private PaymentProvider provider;

    @Column(name = "provider_order_id", nullable = false, unique = true)
    private String providerOrderId;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    @Column(name = "amount_in_paise", nullable = false)
    private long amountInPaise;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentStatus status;
}

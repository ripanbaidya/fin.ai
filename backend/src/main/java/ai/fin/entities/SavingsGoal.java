package ai.fin.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ai.fin.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "savings_goals",
        indexes = {
                @Index(name = "idx_goal_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SavingsGoal extends BaseEntity {

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * The target amount that a user wants to achieve.
     */
    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    /**
     * The amount that a user has saved so far.
     */
    @Column(name = "saved_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal savedAmount = BigDecimal.ZERO;

    /**
     * The deadline by which the user wants to achieve the savings goal.
     */
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    /**
     * The current status of the savings goal
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    /**
     * Any additional notes or information about the savings goal.
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
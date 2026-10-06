package ai.fin.entities;

import ai.fin.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_categories_user_id", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_category_name_user", columnNames = {"name", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "category_type", length = 20)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    /**
     * NULL means system default (visible to all users)
     * NOT NULL = belongs to a specific user (private)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
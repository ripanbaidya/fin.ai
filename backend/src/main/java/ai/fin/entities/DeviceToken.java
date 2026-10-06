package ai.fin.entities;

import ai.fin.enums.DevicePlatform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "device_tokens",
        indexes = {
                @Index(name = "idx_device_tokens_user_id", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_device_tokens_token", columnNames = "token")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DeviceToken extends BaseEntity {

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 10)
    private DevicePlatform platform = DevicePlatform.WEB;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

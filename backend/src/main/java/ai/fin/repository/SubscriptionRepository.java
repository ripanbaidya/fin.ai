package ai.fin.repository;

import ai.fin.entities.Subscription;
import ai.fin.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    Optional<Subscription> findByUser_IdAndStatus(String userId, SubscriptionStatus status);

    boolean existsByUser_IdAndStatus(String userId, SubscriptionStatus status);
}

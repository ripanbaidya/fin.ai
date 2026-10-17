package ai.fin.repository;

import ai.fin.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    Optional<PaymentTransaction> findByProviderOrderId(String providerOrderId);

    List<PaymentTransaction> findByUser_IdOrderByCreatedAtDesc(String userId);
}

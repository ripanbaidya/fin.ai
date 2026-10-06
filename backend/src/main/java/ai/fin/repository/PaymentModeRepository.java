package ai.fin.repository;

import ai.fin.entities.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentModeRepository extends JpaRepository<PaymentMode, String> {

    void deleteAllByUser_Id(String userId);

    /**
     * Retrieves all payment modes visible to a user, including system defaults (user is null) and the user's
     * own payment modes.
     */
    @Query("select p from PaymentMode p " +
            "where (p.user is null or p.user.id = :userId) " +
            "order by p.name asc")
    List<PaymentMode> findAllVisibleToUser(@Param("userId") String userId);

    /**
     * Checks if a payment mode with the given name already exists for the user or as a system default.
     * Used to prevent duplicate payment mode names while creating a new payment mode.
     */
    @Query("select (count(p) > 0) from PaymentMode p " +
            "where lower(p.name) = lower(:name) " +
            "and (p.user.id = :userId or p.user is null)")
    boolean isNameTaken(@Param("userId") String userId, @Param("name") String name);

    /**
     * Checks if another payment mode with the same name already exists (excluding the current payment mode ID).
     * Used when updating an existing payment mode.
     */
    @Query("select (count(p) > 0) from PaymentMode p " +
            "where lower(p.name) = lower(:name) " +
            "and p.id <> :paymentModeId " +
            "and (p.user.id = :userId or p.user is null)")
    boolean isNameTakenExcludingId(@Param("userId") String userId,
                                   @Param("name") String name,
                                   @Param("paymentModeId") String paymentModeId);

    /**
     * Finds a payment mode by ID that belongs to the specified user.
     */
    Optional<PaymentMode> findByIdAndUser_Id(String id, String userId);
}
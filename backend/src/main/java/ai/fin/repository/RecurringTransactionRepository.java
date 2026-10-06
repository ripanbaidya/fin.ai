package ai.fin.repository;

import ai.fin.entities.RecurringTransaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, String> {

    void deleteAllByUser_Id(String userId);

    /**
     * Fetches all active recurring transactions due for execution on or before the given date.
     * Used by the scheduler to determine which rules to process.
     *
     * @param today the current date used as the execution threshold
     * @return list of due {@link RecurringTransaction} entities
     */
    @EntityGraph(attributePaths = {"category", "paymentMode"})
    @Query("""
                select r from RecurringTransaction r
                where r.isActive = true
                and r.nextExecutionDate <= :today
                and (r.endDate is null or r.endDate >= :today)
            """)
    List<RecurringTransaction> findDueTransactions(@Param("today") LocalDate today);

    /**
     * Returns all active recurring transactions belonging to the given user.
     *
     * @param userId the user's ID
     * @return list of active {@link RecurringTransaction} entities for the user
     */
    @EntityGraph(attributePaths = {"category", "paymentMode"})
    List<RecurringTransaction> findByUser_IdAndIsActiveTrue(String userId);

    /**
     * Returns all recurring transactions for a user, including inactive ones.
     *
     * @param userId the user's ID
     * @return list of all {@link RecurringTransaction} entities for the user
     */
    @EntityGraph(attributePaths = {"category", "paymentMode"})
    List<RecurringTransaction> findByUser_Id(String userId);

    /**
     * Finds a specific recurring transaction by its ID, scoped to the given user.
     * Returns empty if the transaction doesn't exist or doesn't belong to the user.
     *
     * @param id     the recurring transaction ID
     * @param userId the user's ID
     * @return an {@link Optional} containing the matching entity, or empty if not found
     */
    @EntityGraph(attributePaths = {"category", "paymentMode"})
    Optional<RecurringTransaction> findByIdAndUser_Id(String id, String userId);

    /**
     * Fetches active recurring transactions for a user that are forecastable within a date range.
     * A transaction is forecastable if it has started and has a pending execution before {@code forecastUntil},
     * and its end date has not passed.
     *
     * @param userId        the user's ID
     * @param today         the current date, used to filter out expired rules
     * @param forecastUntil the upper bound of the forecast window
     * @return list of forecastable {@link RecurringTransaction} entities
     */
    @EntityGraph(attributePaths = {"category", "paymentMode"})
    @Query("""
                select r from RecurringTransaction r
                where r.user.id = :userId
                and r.isActive = true
                and r.startDate <= :forecastUntil
                and r.nextExecutionDate <= :forecastUntil
                and (r.endDate is null or r.endDate >= :today)
            """)
    List<RecurringTransaction> findForecastableByUser(
            @Param("userId") String userId,
            @Param("today") LocalDate today,
            @Param("forecastUntil") LocalDate forecastUntil
    );
}

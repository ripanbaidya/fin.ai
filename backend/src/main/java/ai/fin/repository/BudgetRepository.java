package ai.fin.repository;

import ai.fin.entities.Budget;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, String> {

    void deleteAllByUser_Id(String userId);

    /**
     * Returns all budgets for a user in the given month, eagerly fetching category.
     */
    @EntityGraph(attributePaths = {"category"})
    List<Budget> findByUser_IdAndMonth(String userId, YearMonth month);

    /**
     * Finds a budget by its ID, scoped to the given user, eagerly fetching category.
     */
    @EntityGraph(attributePaths = {"category"})
    Optional<Budget> findByIdAndUser_Id(String id, String userId);

    /**
     * Checks whether a budget already exists for the given user, category, and month.
     * Used to prevent duplicate budget entries.
     */
    boolean existsByUser_IdAndCategoryIdAndMonth(String userId, String categoryId, YearMonth month);

    /**
     * Check budget by user, category, and month, eagerly fetching category.
     */
    @EntityGraph(attributePaths = {"category"})
    Optional<Budget> findByUser_IdAndCategoryIdAndMonth(String userId, String categoryId, YearMonth month);
}

package ai.fin.repository;

import ai.fin.entities.SavingsGoal;
import ai.fin.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, String> {

    /**
     * Delete all savings goals associated with a user.
     */
    void deleteAllByUser_Id(String userId);

    /**
     * Find all savings goals associated with a user.
     */
    List<SavingsGoal> findByUser_Id(String userId);

    /**
     * Find a savings goal by its ID and associated user ID.
     */
    Optional<SavingsGoal> findByIdAndUser_Id(String id, String userId);

    /**
     * Find savings goals by their status and deadline.
     */
    List<SavingsGoal> findByStatusAndDeadlineBefore(GoalStatus status, LocalDate date);
}
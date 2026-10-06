package ai.fin.service;

import ai.fin.dto.savingsgoal.ContributeRequest;
import ai.fin.dto.savingsgoal.GoalProgressResponse;
import ai.fin.dto.savingsgoal.SavingsGoalRequest;

import java.util.List;

public interface SavingsGoalService {

    /**
     * Creates a new savings goal for the current user.
     */
    GoalProgressResponse create(String userId, SavingsGoalRequest request);

    /**
     * Retrieves all savings goals for the current user.
     */
    List<GoalProgressResponse> getAll(String userId);

    /**
     * Contributes an amount towards a savings goal.
     * Updates the saved amount and marks the goal as ACHIEVED if the target amount
     * is reached or exceeded.
     */
    GoalProgressResponse contribute(String userId, String goalId, ContributeRequest request);

    /**
     * Retrieves the progress of a specific savings goal.
     */
    GoalProgressResponse getProgress(String userId, String goalId);

    /**
     * Deletes a savings goal owned by the current user.
     */
    void delete(String userId, String goalId);

    /**
     * Marks all expired goals as FAILED.
     * A goal is considered expired if - Status is IN_PROGRESS or Deadline is before the current date
     * This will invoked by a scheduled job.
     */
    void markExpiredGoalsAsFailed();
}

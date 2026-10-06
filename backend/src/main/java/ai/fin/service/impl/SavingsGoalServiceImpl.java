package ai.fin.service.impl;

import ai.fin.dto.savingsgoal.ContributeRequest;
import ai.fin.dto.savingsgoal.GoalProgressResponse;
import ai.fin.dto.savingsgoal.SavingsGoalRequest;
import ai.fin.entities.SavingsGoal;
import ai.fin.entities.User;
import ai.fin.enums.GoalStatus;
import ai.fin.enums.NotificationType;
import ai.fin.mapper.SavingsGoalMapper;
import ai.fin.repository.SavingsGoalRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.NotificationService;
import ai.fin.service.SavingsGoalService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.SavingsGoalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final UserRepository userRepository;
    private final SavingsGoalRepository goalRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public GoalProgressResponse create(String userId, SavingsGoalRequest request) {
        User user = currentUser(userId);

        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setTitle(request.title());
        goal.setTargetAmount(request.targetAmount());
        goal.setSavedAmount(BigDecimal.ZERO);
        goal.setDeadline(request.deadline());
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setNote(request.note());

        SavingsGoal saved = goalRepository.save(goal);

        log.info("Created new savings goal for userId='{}', goalId='{}', title='{}'",
                userId, saved.getId(), saved.getTitle());
        return SavingsGoalMapper.toProgressResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalProgressResponse> getAll(String userId) {
        return goalRepository.findByUser_Id(userId)
                .stream()
                .map(SavingsGoalMapper::toProgressResponse)
                .toList();
    }

    @Override
    @Transactional
    public GoalProgressResponse contribute(String userId, String goalId, ContributeRequest request) {
        SavingsGoal goal = findOwnedOrThrow(goalId, userId);

        if (goal.getStatus() == GoalStatus.ACHIEVED) {
            throw new SavingsGoalException(ErrorCode.GOAL_ALREADY_ACHIEVED);
        }
        if (goal.getStatus() == GoalStatus.FAILED) {
            throw new SavingsGoalException(ErrorCode.GOAL_ALREADY_FAILED);
        }

        goal.setSavedAmount(goal.getSavedAmount().add(request.amount()));

        // Check if user has reached the goal
        if (goal.getSavedAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.ACHIEVED);

            log.info("Goal {} achieved by user {}", goalId, userId);
            notificationService.sendNotification(
                    userId,
                    "Savings Goal Achieved! 🎉",
                    String.format("Congratulations! You've reached your savings goal '%s' of %s.",
                            goal.getTitle(), goal.getTargetAmount()),
                    NotificationType.SAVINGS_GOAL_ACHIEVED,
                    Map.of("goalId", goal.getId(), "type", NotificationType.SAVINGS_GOAL_ACHIEVED.name())
            );
        }

        goalRepository.save(goal);
        return SavingsGoalMapper.toProgressResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalProgressResponse getProgress(String userId, String goalId) {
        return SavingsGoalMapper.toProgressResponse(findOwnedOrThrow(goalId, userId));
    }

    @Override
    @Transactional
    public void delete(String userId, String goalId) {
        SavingsGoal goal = findOwnedOrThrow(goalId, userId);
        goalRepository.delete(goal);
    }

    @Override
    @Transactional
    public void markExpiredGoalsAsFailed() {
        List<SavingsGoal> expired = goalRepository
                .findByStatusAndDeadlineBefore(GoalStatus.IN_PROGRESS, LocalDate.now());

        expired.forEach(goal -> {
            goal.setStatus(GoalStatus.FAILED);

            log.info("Goal {} marked as FAILED — deadline passed", goal.getId());
            notificationService.sendNotification(
                    goal.getUser().getId(),
                    "Savings Goal Expired",
                    String.format("The deadline for your savings goal '%s' has passed without reaching the target.",
                            goal.getTitle()),
                    NotificationType.SAVINGS_GOAL_FAILED,
                    Map.of("goalId", goal.getId(), "type", NotificationType.SAVINGS_GOAL_FAILED.name())
            );
        });

        goalRepository.saveAll(expired);
    }

    // Helper methods

    private User currentUser(String userId) {
        return userRepository.getReferenceById(userId);
    }

    private SavingsGoal findOwnedOrThrow(String id, String userId) {
        return goalRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> {
                    boolean exists = goalRepository.existsById(id);
                    return exists
                            ? new SavingsGoalException(ErrorCode.GOAL_ACCESS_DENIED)
                            : new SavingsGoalException(ErrorCode.GOAL_NOT_FOUND);
                });
    }
}

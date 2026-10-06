package ai.fin.service.impl;

import ai.fin.dto.budget.BudgetRequest;
import ai.fin.dto.budget.BudgetResponse;
import ai.fin.dto.budget.BudgetStatusResponse;
import ai.fin.entities.Budget;
import ai.fin.entities.Category;
import ai.fin.entities.User;
import ai.fin.mapper.BudgetMapper;
import ai.fin.repository.BudgetRepository;
import ai.fin.repository.TransactionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.BudgetService;
import ai.fin.service.CategoryService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.BudgetException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final CategoryService categoryService;

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public BudgetResponse create(String userId, BudgetRequest request) {
        Category category = categoryService.findById(request.categoryId());

        // Prevent duplicate budget for same category + month
        if (budgetRepository.existsByUser_IdAndCategoryIdAndMonth(userId,
                request.categoryId(), request.month())) {

            throw new BudgetException(ErrorCode.BUDGET_DUPLICATE);
        }

        Budget budget = new Budget();
        budget.setUser(currentUser(userId));
        budget.setCategory(category);
        budget.setMonth(request.month());
        budget.setLimitAmount(request.limitAmount());
        budget.setAlertThreshold(request.alertThreshold());

        Budget saved = budgetRepository.save(budget);
        log.info("Budget created successfully for user: {}", userId);

        return BudgetMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getByMonth(String userId, YearMonth month) {
        return budgetRepository.findByUser_IdAndMonth(userId, month)
                .stream()
                .map(BudgetMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetStatusResponse getStatus(String userId, String budgetId) {
        Budget budget = findOwnedOrThrow(budgetId, userId);

        BigDecimal spent = transactionRepository.sumExpensesByCategoryAndMonth(
                userId, budget.getCategory().getId(), budget.getMonth().atDay(1), budget.getMonth().atEndOfMonth());

        BigDecimal limit = budget.getLimitAmount();
        BigDecimal remaining = limit.subtract(spent);
        double usagePct = spent.divide(limit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return BudgetStatusResponse.builder()
                .budgetId(budget.getId())
                .categoryName(budget.getCategory().getName())
                .month(budget.getMonth())
                .limitAmount(limit)
                .spentAmount(spent)
                .remainingAmount(remaining)
                .usagePercentage(usagePct)
                .thresholdBreached(usagePct >= budget.getAlertThreshold())
                .limitBreached(spent.compareTo(limit) > 0)
                .build();
    }

    @Override
    @Transactional
    public void delete(String userId, String budgetId) {
        Budget budget = findOwnedOrThrow(budgetId, userId);
        budgetRepository.delete(budget);
    }

    // Helpers

    private User currentUser(String userId) {
        return userRepository.getReferenceById(userId);
    }

    /**
     * Retrieves a budget owned by the given user.
     * If the budget exists but is owned by another user, an access-denied exception is thrown.
     * If it does not exist, a not found exception is thrown.
     *
     * @param budgetId budget identifier
     * @param userId   current user identifier
     * @return budget entity
     * @throws BudgetException if a budget is not found or access is denied
     */
    private Budget findOwnedOrThrow(String budgetId, String userId) {
        return budgetRepository.findByIdAndUser_Id(budgetId, userId)
                .orElseThrow(() -> {
                    boolean exists = budgetRepository.existsById(budgetId);
                    return exists
                            ? new BudgetException(ErrorCode.BUDGET_ACCESS_DENIED)
                            : new BudgetException(ErrorCode.BUDGET_NOT_FOUND);
                });
    }
}

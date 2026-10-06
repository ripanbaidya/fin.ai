package ai.fin.service.impl;

import ai.fin.dto.recurring.*;
import ai.fin.dto.transaction.CreateTransactionRequest;
import ai.fin.entities.Category;
import ai.fin.entities.PaymentMode;
import ai.fin.entities.RecurringTransaction;
import ai.fin.entities.User;
import ai.fin.enums.NotificationType;
import ai.fin.enums.RecurringFrequency;
import ai.fin.enums.TxnType;
import ai.fin.mapper.RecurringTransactionMapper;
import ai.fin.repository.RecurringTransactionRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.CategoryService;
import ai.fin.service.NotificationService;
import ai.fin.service.PaymentModeService;
import ai.fin.service.RecurringTransactionService;
import ai.fin.service.TransactionService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.RecurringTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final CategoryService categoryService;
    private final PaymentModeService paymentModeService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

    private final UserRepository userRepository;
    private final RecurringTransactionRepository recurringRepository;

    @Override
    @Transactional
    public RecurringTransactionResponse create(String userId, RecurringTransactionRequest request) {
        log.debug("Creating recurring transaction for user: {}", userId);
        User user = currentUser(userId);

        validateDateRange(request.startDate(), request.endDate());

        // Build & save the recurring transaction
        RecurringTransaction recurring = buildRecurringTransaction(request, user);
        recurring = recurringRepository.saveAndFlush(recurring);

        log.info("Recurring transaction created with id: {}, user: {}", recurring.getId(), userId);
        return RecurringTransactionMapper.toResponse(recurring);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecurringTransactionResponse> getAllByUser(String userId) {
        return recurringRepository
                .findByUser_IdAndIsActiveTrue(userId).stream()
                .map(RecurringTransactionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecurringTransactionResponse getById(String userId, String recurringTxnId) {
        RecurringTransaction recurring = findOwnedOrThrow(recurringTxnId, userId);
        return RecurringTransactionMapper.toResponse(recurring);
    }

    @Override
    @Transactional
    public RecurringTransactionResponse update(String userId, String recurringTxnId, UpdateRecurringTransactionRequest request) {
        RecurringTransaction recurring = findOwnedOrThrow(recurringTxnId, userId);

        updateIfPresent(request.title(), recurring::setTitle);
        updateIfPresent(request.amount(), recurring::setAmount);
        updateIfPresent(request.frequency(), recurring::setFrequency);
        updateIfPresent(request.note(), recurring::setNote);

        if (request.endDate() != null) {
            validateDateRange(recurring.getStartDate(), request.endDate());
            recurring.setEndDate(request.endDate());
        }

        if (request.categoryId() != null) {
            recurring.setCategory(resolveCategory(request.categoryId()));
        }

        if (request.paymentModeId() != null) {
            recurring.setPaymentMode(resolvePaymentMode(request.paymentModeId()));
        }

        recurring = recurringRepository.save(recurring);
        log.info("Recurring transaction updated with id: {}, user: {}", recurring.getId(), userId);

        return RecurringTransactionMapper.toResponse(recurring);
    }

    @Override
    @Transactional
    public void deactivate(String userId, String recurringTxnId) {
        RecurringTransaction recurring = findOwnedOrThrow(recurringTxnId, userId);
        if (!recurring.isActive()) {
            throw new RecurringTransactionException(ErrorCode.RECURRING_ALREADY_INACTIVE);
        }

        recurring.setActive(false);
        recurringRepository.save(recurring);
        log.info("Recurring transaction {} deactivated", recurringTxnId);
    }

    @Override
    @Transactional(readOnly = true)
    public ForecastSummaryResponse forecast(String userId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate until = today.plusDays(days);

        List<RecurringTransaction> recurringList =
                recurringRepository.findForecastableByUser(userId, today, until);

        List<ForecastEntryResponse> entries = new ArrayList<>();
        Totals totals = new Totals();

        for (RecurringTransaction recurring : recurringList) {
            LocalDate next = alignToWindow(recurring.getNextExecutionDate(), recurring.getFrequency(), today);

            while (!next.isAfter(until)) {
                if (recurring.getEndDate() == null || !next.isAfter(recurring.getEndDate())) {
                    entries.add(buildForecastEntry(recurring, next));
                    totals.add(recurring.getType(), recurring.getAmount());
                }

                next = computeNextDate(next, recurring.getFrequency());
            }
        }

        entries.sort(Comparator.comparing(ForecastEntryResponse::projectedDate));

        return ForecastSummaryResponse.builder()
                .forecastDays(days)
                .projectedIncome(totals.income())
                .projectedExpense(totals.expense())
                .projectedNetBalance(totals.net())
                .entries(entries)
                .build();
    }

    private LocalDate alignToWindow(LocalDate nextExecutionDate, RecurringFrequency frequency, LocalDate today) {
        LocalDate date = nextExecutionDate;

        while (date.isBefore(today)) {
            date = computeNextDate(date, frequency);
        }

        return date;
    }

    private LocalDate computeNextDate(LocalDate from, RecurringFrequency frequency) {
        return switch (frequency) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }

    @Override
    @Transactional
    public void processDueRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueTransactions = recurringRepository.findDueTransactions(today);

        log.info("Processing {} due recurring transactions", dueTransactions.size());

        for (RecurringTransaction recurring : dueTransactions) {
            try {
                createTransactionFromRecurring(recurring.getUser().getId(), recurring, today);
                LocalDate nextExecution = computeNextDate(today, recurring.getFrequency());

                // Checking if the next execution date is after the end date means that recurring
                // has end, so deactivate it.
                if (recurring.getEndDate() != null && nextExecution.isAfter(recurring.getEndDate())) {
                    recurring.setActive(false);
                    log.info("Recurring {} completed", recurring.getId());
                } else {
                    // Set the next execution date
                    recurring.setNextExecutionDate(nextExecution);
                }

                recurringRepository.save(recurring);

                notificationService.sendNotification(
                        recurring.getUser().getId(),
                        "Recurring Transaction Processed",
                        String.format("Recurring transaction '%s' of %s was automatically processed.",
                                recurring.getTitle(), recurring.getAmount()),
                        NotificationType.RECURRING_TRANSACTION_DUE,
                        Map.of("recurringId", recurring.getId(), "type", NotificationType.RECURRING_TRANSACTION_DUE.name())
                );

            } catch (Exception ex) {
                log.error("Failed processing recurring {}", recurring.getId(), ex);
            }
        }
    }

    private ForecastEntryResponse buildForecastEntry(RecurringTransaction recurring, LocalDate date) {
        return ForecastEntryResponse.builder()
                .projectedDate(date)
                .title(recurring.getTitle())
                .amount(recurring.getAmount())
                .type(recurring.getType())
                .categoryName(recurring.getCategory() != null ? recurring.getCategory().getName() : null)
                .build();
    }

    private RecurringTransaction buildRecurringTransaction(RecurringTransactionRequest request, User user) {
        RecurringTransaction recurring = new RecurringTransaction();

        recurring.setTitle(request.title());
        recurring.setAmount(request.amount());
        recurring.setType(request.type());
        recurring.setFrequency(request.frequency());
        recurring.setStartDate(request.startDate());
        recurring.setEndDate(request.endDate());
        recurring.setNextExecutionDate(request.startDate());
        recurring.setActive(true);
        recurring.setNote(request.note());

        recurring.setUser(user);
        recurring.setCategory(resolveCategory(request.categoryId()));
        recurring.setPaymentMode(resolvePaymentMode(request.paymentModeId()));

        return recurring;
    }

    private void createTransactionFromRecurring(String userId, RecurringTransaction recurring, LocalDate date) {
        transactionService.create(
                userId, new CreateTransactionRequest(
                        recurring.getAmount(),
                        recurring.getType(),
                        date,
                        recurring.getNote(),
                        recurring.getCategory() != null ? recurring.getCategory().getId() : null,
                        recurring.getPaymentMode() != null ? recurring.getPaymentMode().getId() : null
                ));
    }

    // Helpers

    private User currentUser(String userId) {
        return userRepository.getReferenceById(userId);
    }

    private Category resolveCategory(String id) {
        return id == null ? null : categoryService.findById(id);
    }

    private PaymentMode resolvePaymentMode(String id) {
        return id == null ? null : paymentModeService.findById(id);
    }

    private <T> void updateIfPresent(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private RecurringTransaction findOwnedOrThrow(String id, String userId) {
        return recurringRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> {
                    boolean exists = recurringRepository.existsById(id);
                    return exists
                            ? new RecurringTransactionException(ErrorCode.RECURRING_ACCESS_DENIED)
                            : new RecurringTransactionException(ErrorCode.RECURRING_NOT_FOUND);
                });
    }

    public void validateDateRange(LocalDate start, LocalDate end) {
        if (end != null && !end.isAfter(start)) {
            throw new RecurringTransactionException(ErrorCode.RECURRING_END_DATE_BEFORE_START);
        }
    }

    private static class Totals {

        private BigDecimal income = BigDecimal.ZERO;
        private BigDecimal expense = BigDecimal.ZERO;

        void add(TxnType type, BigDecimal amount) {
            if (type == TxnType.INCOME) {
                income = income.add(amount);
            } else {
                expense = expense.add(amount);
            }
        }

        BigDecimal income() {
            return income;
        }

        BigDecimal expense() {
            return expense;
        }

        BigDecimal net() {
            return income.subtract(expense);
        }
    }
}

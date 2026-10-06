package ai.fin.service.impl;

import ai.fin.dto.dashboard.*;
import ai.fin.enums.TxnType;
import ai.fin.mapper.TransactionMapper;
import ai.fin.repository.TransactionRepository;
import ai.fin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    // TODO - Implement Caching at Dashboard to Avoid Unnecessary db call at each request
    public DashboardResponse getDashboard(String userId, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        // Calculate summary - income, expense and netBalance
        BigDecimal totalIncome = transactionRepository
                .sumByUserAndTypeAndDateBetween(userId, TxnType.INCOME, from, to);
        BigDecimal totalExpense = transactionRepository
                .sumByUserAndTypeAndDateBetween(userId, TxnType.EXPENSE, from, to);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        // Build the summary
        DashboardSummary summary = new DashboardSummary(totalIncome, totalExpense, netBalance);

        // Calculate category-wise breakdowns
        List<CategoryBreakdownResponse> expenseByCategory = TransactionMapper.toBreakdownResponse(
                transactionRepository.findCategoryBreakdown(userId, TxnType.EXPENSE, from, to),
                totalExpense
        );
        List<CategoryBreakdownResponse> incomeByCategory = TransactionMapper.toBreakdownResponse(
                transactionRepository.findCategoryBreakdown(userId, TxnType.INCOME, from, to),
                totalIncome
        );

        List<DailyTrendItem> dailyTrend = transactionRepository.findDailyTrend(userId, from, to);
        List<TopExpenseItem> topExpenses = transactionRepository.findTop5Expenses(userId, from, to);

        return new DashboardResponse(month.format(MONTH_FMT), summary, expenseByCategory,
                incomeByCategory, dailyTrend, topExpenses);
    }
}

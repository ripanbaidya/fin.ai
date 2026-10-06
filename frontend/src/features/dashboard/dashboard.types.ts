export interface DashboardSummary {
    totalIncome: number;
    totalExpense: number;
    netBalance: number;
}

export interface CategoryBreakdownItem {
    categoryName: string;
    amount: number;
}

export interface CategoryBreakdownResponse {
    categoryName: string;
    amount: number;
    percentage: number;
}

export interface DailyTrendItem {
    date: string;
    income: number;
    expense: number;
}

export interface TopExpenseItem {
    id: string;
    amount: number;
    categoryName: string | null;
    note: string | null;
    date: string;
}

export interface DashboardResponse {
    month: string;
    summary: DashboardSummary;
    expenseByCategory: CategoryBreakdownResponse[];
    incomeByCategory: CategoryBreakdownResponse[];
    dailyTrend: DailyTrendItem[];
    topExpenses: TopExpenseItem[];
}
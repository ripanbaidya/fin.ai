export interface BudgetResponse {
  id: string;
  categoryName: string;
  categoryId: string;
  month: string; // "yyyy-MM"
  limitAmount: number;
  alertThreshold: number; // percentage e.g. 80
}

export interface BudgetStatusResponse {
  budgetId: string;
  categoryName: string;
  month: string;
  limitAmount: number;
  spentAmount: number;
  remainingAmount: number;
  usagePercentage: number;
  thresholdBreached: boolean;
  limitBreached: boolean;
}

export interface CreateBudgetRequest {
  categoryId: string;
  month: string; // "yyyy-MM"
  limitAmount: number;
  alertThreshold: number; // default 80
}
import type { TxnType } from "../transactions/transaction.types";

export type RecurringFrequency = "DAILY" | "WEEKLY" | "MONTHLY" | "YEARLY";

export interface RecurringTransactionResponse {
  id: string;
  title: string;
  amount: number;
  type: TxnType;
  frequency: RecurringFrequency;
  startDate: string;       // "yyyy-MM-dd"
  endDate: string | null;  // "yyyy-MM-dd" or null
  nextExecutionDate: string;
  isActive: boolean;
  note: string | null;
  categoryName: string | null;
  paymentModeName: string | null;
  createdAt: string;       // "yyyy-MM-dd HH:mm:ss"
}

export interface ForecastEntryResponse {
  projectedDate: string;   // "yyyy-MM-dd"
  title: string;
  amount: number;
  type: TxnType;
  categoryName: string | null;
}

export interface ForecastSummaryResponse {
  forecastDays: number;
  projectedIncome: number;
  projectedExpense: number;
  projectedNetBalance: number;
  entries: ForecastEntryResponse[];
}

export interface CreateRecurringTransactionRequest {
  title: string;
  amount: number;
  type: TxnType;
  frequency: RecurringFrequency;
  startDate: string;        // "yyyy-MM-dd"
  endDate?: string;         // optional "yyyy-MM-dd"
  note?: string;
  categoryId: string;
  paymentModeId: string;
}

export interface UpdateRecurringTransactionRequest {
  title?: string;
  amount?: number;
  frequency?: RecurringFrequency;
  endDate?: string;
  note?: string;
  categoryId?: string;
  paymentModeId?: string;
}
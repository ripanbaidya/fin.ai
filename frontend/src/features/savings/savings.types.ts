export type GoalStatus = 'IN_PROGRESS' | 'ACHIEVED' | 'FAILED';

export interface GoalProgressResponse {
  id: string;
  title: string;
  targetAmount: number;
  savedAmount: number;
  remainingAmount: number;
  progressPercentage: number;
  daysRemaining: number;
  status: GoalStatus;
  deadline: string; // "yyyy-MM-dd"
}

export interface CreateSavingsGoalRequest {
  title: string;
  targetAmount: number;
  deadline: string; // "yyyy-MM-dd"
  note?: string;
}

export interface ContributeRequest {
  amount: number;
}
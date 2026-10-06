/* Data for Home page */
export interface Feature {
  title: string;
  description: string;
}

export interface Step {
  number: string;
  title: string;
  description: string;
}

/* Features list */
export const FEATURES: Feature[] = [
  {
    title: "AI financial insights",
    description:
      "Ask questions in everyday language and get answers grounded in your recorded transactions, budgets, and goals.",
  },
  {
    title: "A clear dashboard",
    description:
      "See income, expenses, balances, and category trends together, with a view you can filter by month.",
  },
  {
    title: "Budget Alerts",
    description:
      "Set monthly limits by category and get notified when you are nearing a limit.",
  },
  {
    title: "Savings Goals",
    description:
      "Set a target and deadline, add contributions, and keep your progress in view.",
  },
  {
    title: "Recurring Transactions",
    description:
      "Schedule regular income or expenses and keep upcoming transactions organised.",
  },
  {
    title: "CSV Export",
    description:
      "Download your transaction history as a CSV for your records or spreadsheet.",
  },
];

/* How-it-works steps */
export const STEPS: Step[] = [
  {
    number: "01",
    title: "Create your account",
    description:
      "Sign up free and verify your email with a 6-digit OTP. Takes under a minute.",
  },
  {
    number: "02",
    title: "Log your transactions",
    description:
      "Add income and expenses manually, or automate repeating ones with recurring rules.",
  },
  {
    number: "03",
    title: "Set budgets and goals",
    description:
      "Define monthly spending caps per category and long-term savings milestones.",
  },
  {
    number: "04",
    title: "Ask your AI assistant",
    description:
      "Chat in natural language. fin.ai pulls your real data to give grounded, personalised answers.",
  },
];

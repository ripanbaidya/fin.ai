import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { savingsService } from "../savingsService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";

import GoalForm from "../components/GoalForm";
import GoalCard from "../components/GoalCard";
import ContributeModal from "../components/ContributeModal";

import type {
  CreateSavingsGoalRequest,
  GoalProgressResponse,
  GoalStatus,
} from "../savings.types";

/* Page */
export default function SavingsPage() {
  const queryClient = useQueryClient();

  const [showForm, setShowForm] = useState(false);
  const [createError, setCreateError] = useState<AppError | null>(null);

  // null = modal closed, GoalProgressResponse = contributing to that goal
  const [contributing, setContributing] = useState<GoalProgressResponse | null>(
    null,
  );
  const [contributeError, setContributeError] = useState<AppError | null>(null);

  // Active filter tab
  const [statusFilter, setStatusFilter] = useState<GoalStatus | "ALL">("ALL");

  // Fetch all goals
  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["savings-goals"],
    queryFn: () => savingsService.getAll(),
  });

  // Create
  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: (data: CreateSavingsGoalRequest) => savingsService.create(data),
    onSuccess: () => {
      setShowForm(false);
      setCreateError(null);
      queryClient.invalidateQueries({ queryKey: ["savings-goals"] });
    },
    onError: (err: AppError) => setCreateError(err),
  });

  // Contribute
  const { mutate: contribute, isPending: isContributing } = useAppMutation({
    mutationFn: ({ id, amount }: { id: string; amount: number }) =>
      savingsService.contribute(id, { amount }),
    onSuccess: () => {
      setContributing(null);
      setContributeError(null);
      queryClient.invalidateQueries({ queryKey: ["savings-goals"] });
    },
    onError: (err: AppError) => setContributeError(err),
  });

  const { mutate: removeGoal, isPending: isDeletingGoal } = useAppMutation({
    mutationFn: (id: string) => savingsService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["savings-goals"] });
    },
    onError: (err: AppError) => setContributeError(err),
  });

  // Derived
  const allGoals = data?.data ?? [];

  const filtered =
    statusFilter === "ALL"
      ? allGoals
      : allGoals.filter((g) => g.status === statusFilter);

  // Counts per status for tab badges
  const counts = {
    ALL: allGoals.length,
    IN_PROGRESS: allGoals.filter((g) => g.status === "IN_PROGRESS").length,
    ACHIEVED: allGoals.filter((g) => g.status === "ACHIEVED").length,
    FAILED: allGoals.filter((g) => g.status === "FAILED").length,
  };

  const tabs: { key: GoalStatus | "ALL"; label: string }[] = [
    { key: "ALL", label: "All" },
    { key: "IN_PROGRESS", label: "In Progress" },
    { key: "ACHIEVED", label: "Achieved" },
    { key: "FAILED", label: "Failed" },
  ];

  return (
    <div className="max-w-3xl mx-auto space-y-5">
      {/* ── Header ── */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-gray-900">Savings Goals</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            Track progress toward your financial targets.
          </p>
        </div>
        <button
          onClick={() => {
            setCreateError(null);
            setShowForm(true);
          }}
          className="text-sm bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
        >
          + New Goal
        </button>
      </div>

      {/* ── Status tabs ── */}
      <div className="flex gap-1 border-b border-gray-200">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setStatusFilter(tab.key)}
            className={`px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px flex items-center gap-1.5 ${
              statusFilter === tab.key
                ? "border-black text-black"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            {tab.label}
            {counts[tab.key] > 0 && (
              <span
                className={`text-xs rounded-full px-1.5 py-0.5 ${
                  statusFilter === tab.key
                    ? "bg-black text-white"
                    : "bg-gray-100 text-gray-500"
                }`}
              >
                {counts[tab.key]}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* ── Content ── */}
      {isLoading ? (
        <div className="flex justify-center py-16">
          <Spinner />
        </div>
      ) : error ? (
        <QueryError error={error} onRetry={refetch} />
      ) : filtered.length === 0 ? (
        <div className="bg-white border border-gray-200 rounded-lg py-16 text-center">
          <p className="text-sm text-gray-400 mb-3">
            {statusFilter === "ALL"
              ? "No savings goals yet."
              : `No ${statusFilter.toLowerCase().replace("_", " ")} goals.`}
          </p>
          {statusFilter === "ALL" && (
            <button
              onClick={() => setShowForm(true)}
              className="text-sm text-black underline underline-offset-2"
            >
              Create your first goal
            </button>
          )}
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {filtered.map((goal) => (
            <GoalCard
              key={goal.id}
              goal={goal}
              onContribute={(g) => {
                setContributeError(null);
                setContributing(g);
              }}
              onDelete={(id) => removeGoal(id)}
              isDeleting={isDeletingGoal}
            />
          ))}
        </div>
      )}

      {/* ── Create panel ── */}
      {showForm && <GoalForm
        open={showForm}
        onClose={() => setShowForm(false)}
        onSubmit={(data) => create(data)}
        isPending={isCreating}
        submitError={createError}
      />}

      {/* ── Contribute modal ── */}
      {contributing && <ContributeModal
        key={contributing.id}
        goal={contributing}
        onClose={() => setContributing(null)}
        onSubmit={(id, amount) => contribute({ id, amount })}
        isPending={isContributing}
        submitError={contributeError}
      />}
    </div>
  );
}

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
  RiCalendarLine,
  RiCheckDoubleLine,
  RiChatSmile2Line,
} from "react-icons/ri";
import { toast } from "sonner";
import { AppError } from "../../../api/errorParser";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";
import { openRazorpayCheckout } from "../razorpayCheckout";
import { subscriptionService } from "../subscriptionService";
import type {
  PaymentHistoryResponse,
  PlanResponse,
  SubscriptionPlan,
  SubscriptionStatusResponse,
  VerifyPaymentRequest,
} from "../subscription.types";
import PaymentHistory from "../components/PaymentHistory";
import PlanCard from "../components/PlanCard";

function formatDate(date: string): string {
  return new Intl.DateTimeFormat("en-IN", { dateStyle: "medium" }).format(
    new Date(date),
  );
}

export default function SubscriptionPage() {
  const queryClient = useQueryClient();
  const [selectedPlan, setSelectedPlan] = useState<SubscriptionPlan | null>(
    null,
  );

  const statusQuery = useAppQuery({
    queryKey: ["subscription-status"],
    queryFn: () => subscriptionService.getStatus(),
  });
  const plansQuery = useAppQuery({
    queryKey: ["subscription-plans"],
    queryFn: () => subscriptionService.getPlans(),
  });
  const historyQuery = useAppQuery({
    queryKey: ["payment-history"],
    queryFn: () => subscriptionService.getPaymentHistory(),
  });

  const { mutate: startCheckout, isPending: isCheckingOut } = useAppMutation({
    mutationFn: async (plan: SubscriptionPlan) => {
      const orderResponse = await subscriptionService.createOrder(plan);
      const payment = await openRazorpayCheckout(orderResponse.data, plan);
      if (!payment) return false;

      const verification: VerifyPaymentRequest = {
        razorpayOrderId: payment.razorpay_order_id,
        razorpayPaymentId: payment.razorpay_payment_id,
        razorpaySignature: payment.razorpay_signature,
      };
      await subscriptionService.verifyPayment(verification);
      return true;
    },
    onSuccess: (verified) => {
      setSelectedPlan(null);
      if (verified) {
        toast.success("Payment verified. Your subscription is active.");
      }
      queryClient.invalidateQueries({ queryKey: ["subscription-status"] });
      queryClient.invalidateQueries({ queryKey: ["payment-history"] });
    },
    onError: (error: AppError) => {
      setSelectedPlan(null);
      toast.error(error.message);
      queryClient.invalidateQueries({ queryKey: ["subscription-status"] });
      queryClient.invalidateQueries({ queryKey: ["payment-history"] });
    },
  });

  const status: SubscriptionStatusResponse | undefined =
    statusQuery.data?.data;
  const plans = [...(plansQuery.data?.data ?? [])].sort(
    (a: PlanResponse, b: PlanResponse) =>
      ["MONTHLY", "YEARLY", "LIFETIME"].indexOf(a.plan) -
      ["MONTHLY", "YEARLY", "LIFETIME"].indexOf(b.plan),
  );
  const history: PaymentHistoryResponse[] = historyQuery.data?.data ?? [];
  const freePromptLimit = status?.freePromptsLimit ?? 0;
  const promptUsage = freePromptLimit > 0
    ? Math.min(100, ((status?.freePromptsUsed ?? 0) / freePromptLimit) * 100)
    : 0;
  const hasActiveSubscription = status?.hasActiveSubscription ?? false;
  const canPurchase =
    Boolean(status) &&
    !statusQuery.isLoading &&
    !statusQuery.error &&
    !hasActiveSubscription;
  const planName = status?.plan
    ? `${status.plan.charAt(0)}${status.plan.slice(1).toLowerCase()}`
    : "Free";

  return (
    <div className="mx-auto w-full min-w-0 max-w-5xl space-y-6">
      <header>
        <h1 className="text-xl font-semibold text-gray-900">
          Subscription &amp; billing
        </h1>
        <p className="mt-0.5 text-sm text-gray-500">
          Review your plan, explore options, and see your payment history.
        </p>
      </header>

      {statusQuery.isLoading ? (
        <div className="flex justify-center rounded-xl border border-gray-200 bg-white py-12">
          <Spinner />
        </div>
      ) : statusQuery.error ? (
        <div className="rounded-xl border border-gray-200 bg-white">
          <QueryError
            error={statusQuery.error}
            onRetry={() => statusQuery.refetch()}
          />
        </div>
      ) : status ? (
        <section className="rounded-xl border border-gray-200 bg-white p-5 sm:p-6">
          <div className="flex flex-col gap-5 sm:flex-row sm:items-start sm:justify-between">
            <div>
              <p className="text-xs font-medium uppercase tracking-wider text-gray-500">
                Current plan
              </p>
              <div className="mt-1 flex flex-wrap items-center gap-2">
                <h2 className="text-lg font-semibold text-gray-900">
                  {planName}
                </h2>
                <span
                  className={`rounded-full px-2.5 py-1 text-xs font-medium ${
                    hasActiveSubscription
                      ? "bg-emerald-50 text-emerald-700"
                      : "bg-gray-100 text-gray-600"
                  }`}
                >
                  {hasActiveSubscription ? "Active" : "Free plan"}
                </span>
              </div>
              {hasActiveSubscription && status.endDate && (
                <p className="mt-2 flex items-center gap-1.5 text-sm text-gray-500">
                  <RiCalendarLine aria-hidden="true" />
                  Access until {formatDate(status.endDate)}
                </p>
              )}
              {hasActiveSubscription && !status.endDate && (
                <p className="mt-2 text-sm text-gray-500">
                  Your lifetime access has no expiry date.
                </p>
              )}
            </div>

            {hasActiveSubscription ? (
              <div className="flex items-center gap-2 rounded-lg bg-emerald-50 px-3 py-2 text-sm font-medium text-emerald-800">
                <RiCheckDoubleLine aria-hidden="true" />
                Subscription active
              </div>
            ) : (
              <div className="w-full rounded-lg bg-gray-50 p-4 sm:max-w-xs">
                <div className="mb-2 flex items-center justify-between text-xs">
                  <span className="flex items-center gap-1.5 font-medium text-gray-700">
                    <RiChatSmile2Line aria-hidden="true" />
                    Free AI prompts
                  </span>
                  <span className="text-gray-500">
                    {status.freePromptsUsed} / {status.freePromptsLimit}
                  </span>
                </div>
                <div
                  className="h-1.5 overflow-hidden rounded-full bg-gray-200"
                  role="progressbar"
                  aria-label="Free AI prompts used"
                  aria-valuemin={0}
                  aria-valuemax={Math.max(status.freePromptsLimit, 1)}
                  aria-valuenow={Math.min(
                    status.freePromptsUsed,
                    status.freePromptsLimit,
                  )}
                >
                  <div
                    className="h-full rounded-full bg-blue-600 transition-[width]"
                    style={{ width: `${promptUsage}%` }}
                  />
                </div>
              </div>
            )}
          </div>
        </section>
      ) : (
        <p role="alert" className="text-sm text-gray-600">
          Subscription details are unavailable. Please try again.
        </p>
      )}

      <section className="space-y-3">
        <div>
          <h2 className="text-base font-semibold text-gray-900">
            Choose a plan
          </h2>
          <p className="mt-0.5 text-sm text-gray-500">
            Secure checkout is provided by Razorpay.
          </p>
        </div>

        {plansQuery.isLoading ? (
          <div className="flex justify-center rounded-xl border border-gray-200 bg-white py-12">
            <Spinner />
          </div>
        ) : plansQuery.error ? (
          <div className="rounded-xl border border-gray-200 bg-white">
            <QueryError
              error={plansQuery.error}
              onRetry={() => plansQuery.refetch()}
            />
          </div>
        ) : plans.length === 0 ? (
          <p role="alert" className="rounded-xl border border-gray-200 bg-white p-5 text-sm text-gray-600">
            No subscription plans are available right now.
          </p>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {plans.map((plan) => (
              <PlanCard
                key={plan.plan}
                plan={plan}
                isCurrent={hasActiveSubscription && status?.plan === plan.plan}
                isSubscriptionActive={hasActiveSubscription}
                canPurchase={canPurchase}
                isCheckoutPending={isCheckingOut}
                isCheckingOut={
                  isCheckingOut && selectedPlan === plan.plan
                }
                onSelect={(selected) => {
                  setSelectedPlan(selected);
                  startCheckout(selected);
                }}
              />
            ))}
          </div>
        )}
      </section>

      {historyQuery.isLoading ? (
        <div className="flex justify-center rounded-xl border border-gray-200 bg-white py-10">
          <Spinner />
        </div>
      ) : historyQuery.error ? (
        <div className="rounded-xl border border-gray-200 bg-white">
          <QueryError
            error={historyQuery.error}
            onRetry={() => historyQuery.refetch()}
          />
        </div>
      ) : (
        <PaymentHistory payments={history} />
      )}
    </div>
  );
}

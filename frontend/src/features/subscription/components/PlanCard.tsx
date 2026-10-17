import {
  RiCheckLine,
  RiCalendarLine,
  RiCalendarCheckLine,
  RiVipCrownLine,
} from "react-icons/ri";
import type { PlanResponse, SubscriptionPlan } from "../subscription.types";

const PLAN_DETAILS: Record<
  SubscriptionPlan,
  {
    name: string;
    description: string;
    icon: typeof RiCalendarLine;
  }
> = {
  MONTHLY: {
    name: "Monthly",
    description: "Flexible access, billed once.",
    icon: RiCalendarLine,
  },
  YEARLY: {
    name: "Yearly",
    description: "Longer-term access at a single upfront price.",
    icon: RiCalendarCheckLine,
  },
  LIFETIME: {
    name: "Lifetime",
    description: "One payment for lifetime access.",
    icon: RiVipCrownLine,
  },
};

const formatPrice = (priceInPaise: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(priceInPaise / 100);

interface PlanCardProps {
  plan: PlanResponse;
  isCurrent: boolean;
  isSubscriptionActive: boolean;
  canPurchase: boolean;
  isCheckoutPending: boolean;
  isCheckingOut: boolean;
  onSelect: (plan: SubscriptionPlan) => void;
}

export default function PlanCard({
  plan,
  isCurrent,
  isSubscriptionActive,
  canPurchase,
  isCheckoutPending,
  isCheckingOut,
  onSelect,
}: PlanCardProps) {
  const details = PLAN_DETAILS[plan.plan];
  const PlanIcon = details.icon;

  return (
    <article className="relative flex flex-col rounded-xl border border-gray-200 bg-white p-5 transition-shadow hover:shadow-sm">
      {plan.plan === "YEARLY" && (
        <span className="absolute right-4 top-4 rounded-full bg-blue-50 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wide text-blue-700">
          Popular
        </span>
      )}

      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gray-100 text-gray-700">
        <PlanIcon size={22} aria-hidden="true" />
      </div>

      <h3 className="mt-4 text-base font-semibold text-gray-900">
        {details.name}
      </h3>

      <p className="mt-1 min-h-10 text-sm leading-5 text-gray-600">
        {details.description}
      </p>

      <p className="mt-4 text-2xl font-bold tracking-tight text-gray-900">
        {formatPrice(plan.priceInPaise)}
      </p>

      <p className="mt-1 text-xs text-gray-600">
        {plan.durationDays === null
          ? "No expiry"
          : `${plan.durationDays} days of access`}
      </p>

      <div className="mt-5 flex items-center gap-2 border-t border-gray-100 pt-4 text-xs text-gray-600">
        <RiCheckLine className="shrink-0 text-emerald-600" aria-hidden="true" />
        {plan.durationDays === null
          ? "One-time payment"
          : `Valid for ${plan.durationDays} days`}
      </div>

      <button
        type="button"
        onClick={() => onSelect(plan.plan)}
        disabled={!canPurchase || isCheckoutPending}
        className="mt-5 inline-flex min-h-10 items-center justify-center rounded-full bg-gray-900 px-4 py-2 text-sm font-medium text-white transition-colors duration-200 hover:bg-black focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-gray-900 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:bg-gray-200 disabled:text-gray-500"
      >
        {isCurrent
          ? "Current plan"
          : isSubscriptionActive
            ? "Active plan"
            : isCheckingOut
              ? "Checkout in progress…"
              : canPurchase
                ? "Choose plan"
                : "Subscription unavailable"}
      </button>
    </article>
  );
}

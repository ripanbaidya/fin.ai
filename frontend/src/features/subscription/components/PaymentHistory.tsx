import { RiReceiptLine } from "react-icons/ri";
import type { PaymentHistoryResponse } from "../subscription.types";

const formatAmount = (amountInPaise: number, currency: string) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency,
    maximumFractionDigits: 2,
  }).format(amountInPaise / 100);

const formatDate = (date: string) =>
  new Intl.DateTimeFormat("en-IN", {
    dateStyle: "medium",
  }).format(new Date(date));

const STATUS_STYLES = {
  CREATED: "bg-amber-50 text-amber-700",
  CAPTURED: "bg-emerald-50 text-emerald-700",
  FAILED: "bg-red-50 text-red-700",
} as const;

interface PaymentHistoryProps {
  payments: PaymentHistoryResponse[];
}

export default function PaymentHistory({ payments }: PaymentHistoryProps) {
  return (
    <section className="overflow-hidden rounded-xl border border-gray-200 bg-white">
      <div className="flex items-center gap-2 border-b border-gray-100 px-5 py-4">
        <RiReceiptLine className="text-gray-500" size={18} aria-hidden="true" />
        <h2 className="text-sm font-semibold text-gray-900">Payment history</h2>
      </div>

      {payments.length === 0 ? (
        <p className="px-5 py-8 text-center text-sm text-gray-500">
          Your completed and pending payments will appear here.
        </p>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full min-w-[600px] text-left text-sm">
            <thead className="bg-gray-50 text-xs font-medium uppercase tracking-wide text-gray-500">
              <tr>
                <th scope="col" className="px-5 py-3">Plan</th>
                <th scope="col" className="px-5 py-3">Date</th>
                <th scope="col" className="px-5 py-3">Amount</th>
                <th scope="col" className="px-5 py-3">Reference</th>
                <th scope="col" className="px-5 py-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {payments.map((payment) => (
                <tr key={payment.id}>
                  <td className="whitespace-nowrap px-5 py-4 font-medium text-gray-900">
                    {payment.plan.charAt(0)}
                    {payment.plan.slice(1).toLowerCase()}
                  </td>
                  <td className="whitespace-nowrap px-5 py-4 text-gray-600">
                    {formatDate(payment.createdAt)}
                  </td>
                  <td className="whitespace-nowrap px-5 py-4 text-gray-600">
                    {formatAmount(payment.amountInPaise, payment.currency)}
                  </td>
                  <td className="max-w-40 truncate px-5 py-4 font-mono text-xs text-gray-500">
                    {payment.providerPaymentId ?? payment.providerOrderId}
                  </td>
                  <td className="whitespace-nowrap px-5 py-4">
                    <span
                      className={`rounded-full px-2.5 py-1 text-xs font-medium ${STATUS_STYLES[payment.status]}`}
                    >
                      {payment.status.charAt(0)}
                      {payment.status.slice(1).toLowerCase()}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

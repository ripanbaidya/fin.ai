import type { CreateOrderResponse, SubscriptionPlan } from "./subscription.types";

interface RazorpayPaymentResponse {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
}

interface RazorpayPaymentFailure {
  error?: {
    description?: string;
  };
}

interface RazorpayOptions {
  key: string;
  amount: number;
  currency: string;
  name: string;
  description: string;
  order_id: string;
  theme: { color: string };
  handler: (response: RazorpayPaymentResponse) => void;
  modal: { ondismiss: () => void };
}

interface RazorpayInstance {
  open: () => void;
  close: () => void;
  on: (
    event: "payment.failed",
    callback: (response: RazorpayPaymentFailure) => void,
  ) => void;
}

interface RazorpayConstructor {
  new (options: RazorpayOptions): RazorpayInstance;
}

declare global {
  interface Window {
    Razorpay?: RazorpayConstructor;
  }
}

const RAZORPAY_SCRIPT_ID = "razorpay-checkout-script";
let scriptLoadPromise: Promise<void> | null = null;

function loadRazorpay(): Promise<void> {
  if (window.Razorpay) return Promise.resolve();
  if (scriptLoadPromise) return scriptLoadPromise;

  scriptLoadPromise = new Promise<void>((resolve, reject) => {
    let script = document.getElementById(
      RAZORPAY_SCRIPT_ID,
    ) as HTMLScriptElement | null;

    const onLoad = () => {
      if (window.Razorpay) {
        resolve();
      } else {
        script?.remove();
        reject(new Error("The payment checkout could not be initialized."));
      }
    };

    const onError = () => {
      script?.remove();
      reject(new Error("Could not load the payment checkout. Please try again."));
    };

    if (!script) {
      script = document.createElement("script");
      script.id = RAZORPAY_SCRIPT_ID;
      script.src = "https://checkout.razorpay.com/v1/checkout.js";
      script.async = true;
      script.addEventListener("load", onLoad, { once: true });
      script.addEventListener("error", onError, { once: true });
      document.body.appendChild(script);
    } else {
      script.addEventListener("load", onLoad, { once: true });
      script.addEventListener("error", onError, { once: true });
    }
  }).catch((error: unknown) => {
    scriptLoadPromise = null;
    throw error;
  });

  return scriptLoadPromise;
}

export async function openRazorpayCheckout(
  order: CreateOrderResponse,
  plan: SubscriptionPlan,
): Promise<RazorpayPaymentResponse | null> {
  await loadRazorpay();

  const Razorpay = window.Razorpay;
  if (!Razorpay) {
    throw new Error("The payment checkout could not be initialized.");
  }

  return new Promise<RazorpayPaymentResponse | null>((resolve, reject) => {
    let settled = false;
    const settle = (callback: () => void) => {
      if (settled) return;
      settled = true;
      callback();
    };

    const checkout = new Razorpay({
      key: order.keyId,
      amount: order.amountInPaise,
      currency: order.currency,
      name: "fin.ai",
      description: `${plan.charAt(0)}${plan.slice(1).toLowerCase()} subscription`,
      order_id: order.orderId,
      theme: { color: "#111827" },
      handler: (response) => settle(() => resolve(response)),
      modal: {
        ondismiss: () => settle(() => resolve(null)),
      },
    });

    checkout.on("payment.failed", (response) => {
      const message =
        response.error?.description || "Payment failed. Please try again.";
      settle(() => {
        checkout.close();
        reject(new Error(message));
      });
    });

    try {
      checkout.open();
    } catch (error) {
      settle(() => reject(error));
    }
  });
}

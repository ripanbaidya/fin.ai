import { useEffect, useRef, useState } from "react";
import { AppError } from "../../../api/errorParser";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { authService } from "../../auth/authService";

interface Props {
  email: string;
  onClose: () => void;
  onVerified: () => void;
}

type Step = "ready" | "code";

const EmailVerifyPanel: React.FC<Props> = ({ email, onClose, onVerified }) => {
  const [step, setStep] = useState<Step>("ready");
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);
  const completionTimeout = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(
    () => () => {
      if (completionTimeout.current) clearTimeout(completionTimeout.current);
    },
    [],
  );

  const { mutate: sendOtp, isPending: isSending } = useAppMutation({
    mutationFn: () => authService.sendOtp({ email }),
    onSuccess: () => {
      setStep("code");
      setOtp("");
      setError("");
    },
    onError: (err: AppError) => setError(err.message),
  });

  const { mutate: resendOtp, isPending: isResending } = useAppMutation({
    mutationFn: () => authService.resendOtp({ email }),
    onSuccess: () => {
      setOtp("");
      setError("");
    },
    onError: (err: AppError) => setError(err.message),
  });

  const { mutate: verifyOtp, isPending: isVerifying } = useAppMutation({
    mutationFn: () => authService.verifyOtp({ email, otp }),
    onSuccess: () => {
      setDone(true);
      completionTimeout.current = setTimeout(() => {
        onVerified();
        onClose();
      }, 1400);
    },
    onError: (err: AppError) => setError(err.message),
  });

  const handleVerify = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (otp.length !== 6) {
      setError("Enter the 6-digit code from your email.");
      return;
    }
    setError("");
    verifyOtp(undefined);
  };

  return (
    <>
      <button
        type="button"
        aria-label="Close email verification"
        className="fixed inset-0 z-30 cursor-default bg-black/30"
        onClick={onClose}
      />
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="verify-email-title"
        className="fixed left-1/2 top-1/2 z-40 w-[calc(100%-2rem)] max-w-md -translate-x-1/2 -translate-y-1/2 rounded-2xl border border-[#e8eaed] bg-white p-5 shadow-[0_12px_40px_rgba(60,64,67,0.20)] sm:p-6"
      >
        <header className="flex items-start justify-between gap-4">
          <div>
            <h2
              id="verify-email-title"
              className="text-lg font-semibold tracking-tight text-[#202124]"
            >
              {done ? "Email verified" : "Verify your email"}
            </h2>
            <p className="mt-1 text-sm text-[#5f6368]">{email}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close"
            className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-lg text-[#5f6368] transition-colors hover:bg-[#f1f3f4] hover:text-[#202124]"
          >
            ×
          </button>
        </header>

        <div className="mt-6">
          {done ? (
            <div
              role="status"
              className="rounded-xl bg-[#e6f4ea] px-4 py-4 text-sm text-[#137333]"
            >
              Your email is verified. You can now use all account features.
            </div>
          ) : step === "ready" ? (
            <div className="space-y-4">
              <p className="text-sm leading-6 text-[#5f6368]">
                We’ll email you a 6-digit verification code. Enter it here to
                confirm your address.
              </p>
              {error && (
                <p
                  role="alert"
                  className="rounded-lg bg-[#fce8e6] px-3 py-2.5 text-sm text-[#b3261e]"
                >
                  {error}
                </p>
              )}
              <button
                type="button"
                onClick={() => {
                  setError("");
                  sendOtp(undefined);
                }}
                disabled={isSending}
                className="inline-flex min-h-11 w-full items-center justify-center rounded-lg bg-[#1a73e8] px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-[#1765cc] disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isSending ? "Sending email…" : "Send verification email"}
              </button>
            </div>
          ) : (
            <form onSubmit={handleVerify} className="space-y-4">
              <div
                role="status"
                className="rounded-lg bg-[#e6f4ea] px-3 py-3 text-sm leading-5 text-[#137333]"
              >
                Verification email sent. Check your inbox and spam folder, then
                enter the code below.
              </div>

              <div>
                <label
                  htmlFor="email-verification-code"
                  className="mb-1.5 block text-sm font-medium text-[#3c4043]"
                >
                  6-digit verification code
                </label>
                <input
                  id="email-verification-code"
                  type="text"
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  pattern="[0-9]{6}"
                  maxLength={6}
                  placeholder="Enter code"
                  value={otp}
                  onChange={(event) => {
                    setOtp(event.target.value.replace(/\D/g, "").slice(0, 6));
                    if (error) setError("");
                  }}
                  autoFocus
                  required
                  className="min-h-12 w-full rounded-lg border border-[#dadce0] px-3 text-center text-lg tracking-[0.3em] text-[#202124] outline-none transition focus:border-[#1a73e8] focus:ring-2 focus:ring-[#1a73e8]/20"
                />
              </div>

              {error && (
                <p
                  role="alert"
                  className="rounded-lg bg-[#fce8e6] px-3 py-2.5 text-sm text-[#b3261e]"
                >
                  {error}
                </p>
              )}

              <button
                type="submit"
                disabled={isVerifying || otp.length !== 6}
                className="inline-flex min-h-11 w-full items-center justify-center rounded-lg bg-[#1a73e8] px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-[#1765cc] disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isVerifying ? "Verifying…" : "Verify email"}
              </button>

              <p className="text-center text-sm text-[#5f6368]">
                Didn’t receive the email?{" "}
                <button
                  type="button"
                  onClick={() => {
                    setError("");
                    resendOtp(undefined);
                  }}
                  disabled={isResending}
                  className="font-medium text-[#1967d2] hover:underline disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {isResending ? "Sending…" : "Resend"}
                </button>
              </p>
            </form>
          )}
        </div>

        <footer className="mt-6 border-t border-[#f1f3f4] pt-4">
          <button
            type="button"
            onClick={onClose}
            className="min-h-10 w-full rounded-lg border border-[#dadce0] px-4 py-2 text-sm font-medium text-[#3c4043] transition-colors hover:bg-[#f8fafd]"
          >
            {done ? "Close" : "Cancel"}
          </button>
        </footer>
      </section>
    </>
  );
};

export default EmailVerifyPanel;

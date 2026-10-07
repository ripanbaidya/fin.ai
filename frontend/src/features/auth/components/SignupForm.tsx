import {useState} from "react";
import {Link} from "react-router-dom";
import {IoEyeOffOutline, IoEyeOutline} from "react-icons/io5";
import {FormError} from "../../../shared/components/ui/FormError";
import {FieldErrorMessage} from "../../../shared/components/ui/FieldErrorMessage";
import {ROUTES} from "../../../routes/routePaths";

interface Props {
  form: { fullName: string; email: string; password: string };
  fieldErrors: Record<string, string>;
  formError: string | null;
  agreeToTerms: boolean;
  isPending: boolean;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onTermsChange: (checked: boolean) => void;
  onSubmit: (e: React.FormEvent) => void;
}

const SignupForm: React.FC<Props> = ({
  form,
  fieldErrors,
  formError,
  agreeToTerms,
  isPending,
  onChange,
  onTermsChange,
  onSubmit,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <>
      {/* Top-level form error */}
      <div>
        <FormError error={formError} />
      </div>

      <form
        onSubmit={onSubmit}
        className="mt-6 space-y-4"
      >
        {/* Full Name */}
        <div>
          <label htmlFor="signup-name" className="mb-1.5 block text-sm font-medium text-[#3c4043]">Full name</label>
          <input
            id="signup-name"
            type="text"
            name="fullName"
            autoComplete="name"
            required
            value={form.fullName}
            placeholder="your full name"
            onChange={onChange}
            className="w-full rounded-full border border-[#e8eaed] bg-[#f8fafd] px-4 py-3 text-sm text-[#202124] outline-none transition placeholder:text-[#9aa0a6] hover:border-[#dadce0] focus:border-[#1a73e8] focus:bg-white focus:ring-4 focus:ring-[#1a73e8]/10 sm:text-base"
          />
          <FieldErrorMessage message={fieldErrors.fullName} />
        </div>

        {/* Email */}
        <div>
          <label htmlFor="signup-email" className="mb-1.5 block text-sm font-medium text-[#3c4043]">Email</label>
          <input
            id="signup-email"
            type="email"
            name="email"
            autoComplete="email"
            required
            value={form.email}
            placeholder="you@example.com"
            onChange={onChange}
            className="w-full rounded-full border border-[#e8eaed] bg-[#f8fafd] px-4 py-3 text-sm text-[#202124] outline-none transition placeholder:text-[#9aa0a6] hover:border-[#dadce0] focus:border-[#1a73e8] focus:bg-white focus:ring-4 focus:ring-[#1a73e8]/10 sm:text-base"
          />
          <FieldErrorMessage message={fieldErrors.email} />
        </div>

        {/* Password */}
        <div>
          <label htmlFor="signup-password" className="mb-1.5 block text-sm font-medium text-[#3c4043]">Password</label>
          <div className="relative">
            <input
              id="signup-password"
              type={showPassword ? "text" : "password"}
              name="password"
              autoComplete="new-password"
              required
              value={form.password}
              placeholder="Create a password"
              onChange={onChange}
              className="w-full rounded-full border border-[#e8eaed] bg-[#f8fafd] px-4 py-3 pr-12 text-sm text-[#202124] outline-none transition placeholder:text-[#9aa0a6] hover:border-[#dadce0] focus:border-[#1a73e8] focus:bg-white focus:ring-4 focus:ring-[#1a73e8]/10 sm:text-base"
            />
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              aria-label={showPassword ? "Hide password" : "Show password"}
              className="absolute right-2.5 top-1/2 -translate-y-1/2 rounded-lg p-2 text-[#5f6368] transition hover:bg-[#eef2f8] hover:text-[#202124] focus:outline-none focus:ring-2 focus:ring-[#1a73e8]/30"
            >
              {showPassword ? (
                <IoEyeOffOutline className="w-5 h-5" />
              ) : (
                <IoEyeOutline className="w-5 h-5" />
              )}
            </button>
          </div>
          <FieldErrorMessage message={fieldErrors.password} />
        </div>

        {/* Terms */}
        <div>
          <label className="flex items-start gap-2 text-sm leading-5 text-[#5f6368]">
            <input
              type="checkbox"
              name="terms"
              checked={agreeToTerms}
              onChange={(e) => onTermsChange(e.target.checked)}
              className="mt-0.5 rounded border-[#dadce0] text-[#1a73e8] focus:ring-[#1a73e8]"
            />
            <span>I agree to the terms and conditions</span>
          </label>
          <FieldErrorMessage message={fieldErrors.terms} />
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={isPending}
          className="flex min-h-12 w-full items-center justify-center rounded-full bg-[#1a73e8] px-4 py-3 text-sm font-medium text-white shadow-[0_4px_12px_rgba(26,115,232,0.18)] transition hover:bg-[#1765cc] focus:outline-none focus:ring-2 focus:ring-[#1a73e8] focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 sm:text-base"
        >
          {isPending ? "Creating account..." : "Create account"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-[#5f6368]">
        Already have an account?{" "}
        <Link
          to={ROUTES.login}
          className="font-medium text-[#1a73e8] transition-colors hover:text-[#174ea6] hover:underline hover:underline-offset-4"
        >
          Login
        </Link>
      </p>
    </>
  );
};

export default SignupForm;

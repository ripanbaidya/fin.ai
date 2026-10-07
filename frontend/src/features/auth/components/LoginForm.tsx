import { Link } from "react-router-dom";
import { useState } from "react";
import { IoEyeOutline, IoEyeOffOutline } from "react-icons/io5";
import { FormError } from "../../../shared/components/ui/FormError";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { ROUTES } from "../../../routes/routePaths";

interface Props {
  email: string;
  password: string;
  fieldErrors: Record<string, string>;
  formError: string | null;
  isPending: boolean;
  onEmailChange: (val: string) => void;
  onPasswordChange: (val: string) => void;
  onSubmit: (e: React.FormEvent) => void;
}

const LoginForm: React.FC<Props> = ({
  email,
  password,
  fieldErrors,
  formError,
  isPending,
  onEmailChange,
  onPasswordChange,
  onSubmit,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="w-full">
      {/* Form Error */}
      <FormError error={formError} />

      <form onSubmit={onSubmit} className="mt-7 space-y-5">
        {/* Email */}
        <div>
          <label
            htmlFor="login-email"
            className="mb-1.5 block text-sm font-medium text-gray-700"
          >
            Email
          </label>

          <input
            id="login-email"
            type="email"
            name="email"
            autoComplete="email"
            required
            value={email}
            placeholder="you@example.com"
            onChange={(e) => onEmailChange(e.target.value)}
            className="w-full rounded-full border border-[#e8eaed] bg-[#f8fafd] px-4 py-3.5 text-sm text-[#202124] outline-none transition placeholder:text-[#9aa0a6] hover:border-[#dadce0] focus:border-[#1a73e8] focus:bg-white focus:ring-4 focus:ring-[#1a73e8]/10 sm:text-base"
          />

          <FieldErrorMessage message={fieldErrors.email} />
        </div>

        {/* Password */}
        <div>
          <label
            htmlFor="login-password"
            className="mb-1.5 block text-sm font-medium text-gray-700"
          >
            Password
          </label>

          <div className="relative">
            <input
              id="login-password"
              type={showPassword ? "text" : "password"}
              name="password"
              autoComplete="current-password"
              required
              value={password}
              placeholder="Enter your password"
              onChange={(e) => onPasswordChange(e.target.value)}
              className="w-full rounded-full border border-[#e8eaed] bg-[#f8fafd] px-4 py-3.5 pr-12 text-sm text-[#202124] outline-none transition placeholder:text-[#9aa0a6] hover:border-[#dadce0] focus:border-[#1a73e8] focus:bg-white focus:ring-4 focus:ring-[#1a73e8]/10 sm:text-base"
            />

            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              aria-label={showPassword ? "Hide password" : "Show password"}
              className="absolute right-2.5 top-1/2 -translate-y-1/2 rounded-lg p-2 text-[#5f6368] transition hover:bg-[#eef2f8] hover:text-[#202124] focus:outline-none focus:ring-2 focus:ring-[#1a73e8]/30"
            >
              {showPassword ? (
                <IoEyeOffOutline className="h-5 w-5" />
              ) : (
                <IoEyeOutline className="h-5 w-5" />
              )}
            </button>
          </div>

          <FieldErrorMessage message={fieldErrors.password} />
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={isPending}
          className="flex min-h-12 w-full items-center justify-center rounded-full bg-[#1a73e8] px-4 py-3 text-sm font-medium text-white shadow-[0_4px_12px_rgba(26,115,232,0.18)] transition hover:bg-[#1765cc] focus:outline-none focus:ring-2 focus:ring-[#1a73e8] focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 sm:text-base"
        >
          {isPending ? "Signing in..." : "Login"}
        </button>
      </form>

      <p className="mt-7 text-center text-sm text-[#5f6368]">
        Don't have an account?{" "}
        <Link
          to={ROUTES.signup}
          className="font-medium text-[#1a73e8] transition-colors hover:text-[#174ea6] hover:underline hover:underline-offset-4"
        >
          Register
        </Link>
      </p>
    </div>
  );
};

export default LoginForm;

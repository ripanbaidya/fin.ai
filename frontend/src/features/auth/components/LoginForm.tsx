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
    <div className="w-full max-w-md mx-auto px-4 sm:px-0">
      {/* Form Error */}
      <FormError error={formError} />

      <form onSubmit={onSubmit} className="mt-6 space-y-4 sm:space-y-5">
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
            className="
              w-full rounded-lg border border-gray-300 bg-white
              px-3.5 py-2.5 sm:px-4 sm:py-3
              text-sm sm:text-base text-gray-900
              outline-none transition
              placeholder:text-gray-400
              focus:border-blue-600 focus:ring-2 focus:ring-blue-100
            "
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
              className="
                w-full rounded-lg border border-gray-300 bg-white
                px-3.5 py-2.5 sm:px-4 sm:py-3 pr-11
                text-sm sm:text-base text-gray-900
                outline-none transition
                placeholder:text-gray-400
                focus:border-blue-600 focus:ring-2 focus:ring-blue-100
              "
            />

            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              aria-label={showPassword ? "Hide password" : "Show password"}
              className="
                absolute right-2.5 top-1/2 -translate-y-1/2
                rounded-md p-1.5
                text-gray-500 transition
                hover:bg-gray-100 hover:text-gray-800
                focus:outline-none focus:ring-2 focus:ring-blue-200
              "
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
          className="
            flex w-full items-center justify-center
            rounded-full bg-blue-600
            px-4 py-2.5 sm:py-3
            text-sm sm:text-base font-medium text-white
            transition
            hover:bg-blue-700
            focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2
            disabled:cursor-not-allowed disabled:opacity-60
          "
        >
          {isPending ? "Signing in..." : "Login"}
        </button>
      </form>

      <p className="mt-5 sm:mt-6 text-center text-sm text-gray-600">
        Don't have an account?{" "}
        <Link
          to={ROUTES.signup}
          className="
            font-medium text-blue-700
            underline underline-offset-4
            hover:text-blue-800
          "
        >
          Register
        </Link>
      </p>
    </div>
  );
};

export default LoginForm;

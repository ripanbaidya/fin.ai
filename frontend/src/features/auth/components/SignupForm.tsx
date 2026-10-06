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
          <label htmlFor="signup-name" className="mb-1.5 block text-sm font-medium text-gray-700">Full name</label>
          <input
            id="signup-name"
            type="text"
            name="fullName"
            autoComplete="name"
            required
            value={form.fullName}
            placeholder="your full name"
            onChange={onChange}
            className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 outline-none placeholder:text-gray-400 focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
          />
          <FieldErrorMessage message={fieldErrors.fullName} />
        </div>

        {/* Email */}
        <div>
          <label htmlFor="signup-email" className="mb-1.5 block text-sm font-medium text-gray-700">Email</label>
          <input
            id="signup-email"
            type="email"
            name="email"
            autoComplete="email"
            required
            value={form.email}
            placeholder="you@example.com"
            onChange={onChange}
            className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 outline-none placeholder:text-gray-400 focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
          />
          <FieldErrorMessage message={fieldErrors.email} />
        </div>

        {/* Password */}
        <div>
          <label htmlFor="signup-password" className="mb-1.5 block text-sm font-medium text-gray-700">Password</label>
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
              className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 pr-12 text-gray-900 outline-none placeholder:text-gray-400 focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
            />
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              aria-label={showPassword ? "Hide password" : "Show password"}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-800"
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
          <label className="flex items-start gap-2 text-sm text-gray-600">
            <input
              type="checkbox"
              name="terms"
              checked={agreeToTerms}
              onChange={(e) => onTermsChange(e.target.checked)}
              className="mt-1 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
            />
            <span>I agree to the terms and conditions</span>
          </label>
          <FieldErrorMessage message={fieldErrors.terms} />
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={isPending}
          className="flex w-full items-center justify-center rounded-full bg-blue-600 px-4 py-3 font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isPending ? "Creating account..." : "Create account"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-gray-600">
        Already have an account?{" "}
        <Link
          to={ROUTES.login}
          className="font-medium text-blue-700 underline underline-offset-4 hover:text-blue-800"
        >
          Login
        </Link>
      </p>
    </>
  );
};

export default SignupForm;

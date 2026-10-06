import { Link } from "react-router-dom";
import { useSignup } from "../hooks/useSignup";
import SignupFormHeader from "../components/SignupFormHeader";
import SignupForm from "../components/SignupForm";
import { ROUTES } from "../../../routes/routePaths";

export default function SignupPage() {
  const {
    form,
    fieldErrors,
    formError,
    agreeToTerms,
    isPending,
    handleChange,
    handleTermsChange,
    handleSubmit,
  } = useSignup();

  return (
    <main className="flex min-h-screen w-full items-center justify-center bg-gray-50 px-4 py-8 font-sans sm:px-6">
        <section className="w-full max-w-sm rounded-xl border border-gray-200 bg-white p-6 shadow-sm sm:p-8">
          <header className="mb-8 text-center">
            <Link to={ROUTES.home} className="text-xl font-bold tracking-tight text-gray-900">
              fin.<span className="text-blue-600">ai</span>
            </Link>
          </header>
          <SignupFormHeader />
          <SignupForm
            form={form}
            fieldErrors={fieldErrors}
            formError={formError}
            agreeToTerms={agreeToTerms}
            isPending={isPending}
            onChange={handleChange}
            onTermsChange={handleTermsChange}
            onSubmit={handleSubmit}
          />
        </section>
    </main>
  );
}

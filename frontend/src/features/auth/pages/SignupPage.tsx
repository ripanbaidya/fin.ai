import { useSignup } from "../hooks/useSignup";
import SignupFormHeader from "../components/SignupFormHeader";
import SignupForm from "../components/SignupForm";
import AuthLayout from "../components/AuthLayout";

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
    <AuthLayout
      imagePosition="right"
      imageUrl="https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=1800&q=85"
      imageHeading="Build a money life that feels like yours."
      imageDescription="Bring spending, budgets, and savings into one thoughtful space—and take the next step with confidence."
    >
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
    </AuthLayout>
  );
}

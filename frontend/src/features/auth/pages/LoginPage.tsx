import { useLogin } from "../hooks/useLogin";
import LoginFormHeader from "../components/LoginFormHeader";
import LoginForm from "../components/LoginForm";
import AuthLayout from "../components/AuthLayout";

export default function LoginPage() {
  const {
    email,
    setEmail,
    password,
    setPassword,
    fieldErrors,
    formError,
    isPending,
    handleSubmit,
  } = useLogin();

  return (
    <AuthLayout
      imagePosition="left"
      imageUrl="/images/login-hero.png"
      imageFocalPoint="30% center"
      imageHeading="Make room for what matters."
      imageDescription="A calmer, clearer view of your money starts here. Pick up right where you left off."
    >
      <LoginFormHeader />
      <LoginForm
        email={email}
        password={password}
        fieldErrors={fieldErrors}
        formError={formError}
        isPending={isPending}
        onEmailChange={setEmail}
        onPasswordChange={setPassword}
        onSubmit={handleSubmit}
      />
    </AuthLayout>
  );
}

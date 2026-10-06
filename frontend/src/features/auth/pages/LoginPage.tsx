import { Link } from "react-router-dom";
import { useLogin } from "../hooks/useLogin";
import LoginFormHeader from "../components/LoginFormHeader";
import LoginForm from "../components/LoginForm";
import { ROUTES } from "../../../routes/routePaths";

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
    <main className="flex min-h-screen w-full items-center justify-center bg-gray-50 px-4 py-8 font-sans sm:px-6">
        <section className="w-full max-w-sm rounded-xl border border-gray-200 bg-white p-6 shadow-sm sm:p-8">
          <header className="mb-8 text-center">
            <Link to={ROUTES.home} className="text-xl font-bold tracking-tight text-gray-900">
              fin
            </Link>
          </header>
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
        </section>
    </main>
  );
}

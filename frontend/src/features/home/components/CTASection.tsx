import { Link } from "react-router-dom";
import { useAuth } from "../../../shared/hooks/useAuth";

const CTASection: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <section className="bg-[#e8f0fe] px-5 py-12 sm:py-16">
      <div className="mx-auto flex max-w-6xl flex-col items-start justify-between gap-7 sm:flex-row sm:items-center">
        <div className="max-w-xl">
          <p className="text-sm font-medium text-[#1967d2]">
            Make a fresh start
          </p>
          <h2 className="mt-2 text-2xl font-semibold tracking-tight text-[#202124] sm:text-3xl">
            Take the next step with your money.
          </h2>
          <p className="mt-2 text-sm leading-6 text-[#5f6368]">
            Bring your spending, budgets, and goals into one clear view.
          </p>
        </div>
        <div className="flex w-full flex-col gap-3 sm:w-auto sm:flex-row">
          <Link
            to={isAuthenticated ? "/dashboard" : "/signup"}
            className="inline-flex min-h-11 items-center justify-center rounded-lg bg-[#1a73e8] px-5 py-2.5 text-sm font-medium text-white transition-colors hover:bg-[#1765cc] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-2 focus-visible:ring-offset-[#e8f0fe]"
          >
            {isAuthenticated ? "Go to dashboard" : "Create an account"}
          </Link>
          {!isAuthenticated && (
            <Link
              to="/login"
              className="inline-flex min-h-11 items-center justify-center rounded-lg border border-[#c6dafc] bg-white/70 px-5 py-2.5 text-sm font-medium text-[#1967d2] transition-colors hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-2 focus-visible:ring-offset-[#e8f0fe]"
            >
              Sign in
            </Link>
          )}
        </div>
      </div>
    </section>
  );
};

export default CTASection;

import { Link } from "react-router-dom";
import { useAuth } from "../../../shared/hooks/useAuth";

const HeroSection: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <section className="bg-[#f8fafd] px-5 py-16 sm:py-20 lg:py-24">
      <div className="mx-auto grid max-w-6xl items-center gap-12 lg:grid-cols-2 lg:gap-16">
        <div className="max-w-xl">
          <p className="mb-5 text-sm font-medium text-[#1a73e8]">
            A clearer way to manage money
          </p>
          <h1 className="text-4xl font-semibold leading-[1.1] tracking-[-0.04em] text-[#202124] sm:text-5xl lg:text-[3.5rem]">
            Feel more confident about your finances.
          </h1>
          <p className="mt-5 max-w-lg text-base leading-7 text-[#5f6368] sm:text-lg">
            See your spending, set realistic budgets, and make progress toward
            the things that matter to you.
          </p>

          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            <Link
              to={isAuthenticated ? "/dashboard" : "/signup"}
              className="inline-flex min-h-12 items-center justify-center gap-2 rounded-lg bg-[#1a73e8] px-5 py-3 text-sm font-medium text-white transition-colors hover:bg-[#1765cc] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-2"
            >
              {isAuthenticated ? "Go to dashboard" : "Get started"}
              <svg
                aria-hidden="true"
                className="h-4 w-4"
                viewBox="0 0 20 20"
                fill="none"
              >
                <path
                  d="M4.167 10h11.666M10 4.167 15.833 10 10 15.833"
                  stroke="currentColor"
                  strokeWidth="1.6"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            </Link>
            {!isAuthenticated && (
              <Link
                to="/login"
                className="inline-flex min-h-12 items-center justify-center rounded-lg border border-[#dadce0] bg-white px-5 py-3 text-sm font-medium text-[#3c4043] transition-colors hover:bg-[#f1f3f4] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-2"
              >
                Sign in
              </Link>
            )}
          </div>

          <p className="mt-5 text-sm text-[#5f6368]">
            Spending, budgets, savings, and helpful AI insights—all together.
          </p>
        </div>

        <div className="mx-auto w-full max-w-[31rem] lg:ml-auto">
          <div className="overflow-hidden rounded-2xl border border-[#e8eaed] bg-white shadow-[0_8px_32px_rgba(60,64,67,0.10)]">
            <div className="flex items-center justify-between border-b border-[#f1f3f4] px-5 py-4">
              <div>
                <p className="text-xs font-medium text-[#5f6368]">
                  Example overview
                </p>
                <h2 className="mt-1 text-sm font-medium text-[#202124]">
                  This month
                </h2>
              </div>
              <span className="rounded-md bg-[#e8f0fe] px-2.5 py-1 text-xs font-medium text-[#1967d2]">
                Preview
              </span>
            </div>

            <div className="p-5 sm:p-6">
              <p className="text-sm text-[#5f6368]">Available balance</p>
              <p className="mt-1 text-3xl font-medium tracking-tight text-[#202124]">
                ₹48,250
              </p>
              <div className="mt-5 grid grid-cols-2 gap-3">
                <div className="rounded-xl bg-[#f8fafd] p-4">
                  <p className="text-xs text-[#5f6368]">Income</p>
                  <p className="mt-1 text-lg font-medium text-[#202124]">
                    ₹62,000
                  </p>
                </div>
                <div className="rounded-xl bg-[#f8fafd] p-4">
                  <p className="text-xs text-[#5f6368]">Expenses</p>
                  <p className="mt-1 text-lg font-medium text-[#202124]">
                    ₹13,750
                  </p>
                </div>
              </div>

              <div className="mt-6">
                <div className="mb-4 flex items-center justify-between">
                  <h3 className="text-sm font-medium text-[#202124]">
                    Spending by category
                  </h3>
                  <span className="text-xs text-[#5f6368]">of ₹13,750</span>
                </div>
                <div className="space-y-4">
                  {[
                    { name: "Home", amount: "₹7,000", width: "51%" },
                    { name: "Food", amount: "₹4,250", width: "31%" },
                    { name: "Transport", amount: "₹2,500", width: "18%" },
                  ].map((category) => (
                    <div key={category.name}>
                      <div className="mb-1.5 flex items-center justify-between text-xs">
                        <span className="text-[#3c4043]">{category.name}</span>
                        <span className="text-[#5f6368]">{category.amount}</span>
                      </div>
                      <div className="h-1.5 overflow-hidden rounded-full bg-[#e8eaed]">
                        <div
                          className="h-full rounded-full bg-[#4285f4]"
                          style={{ width: category.width }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
          <p className="mt-3 text-center text-xs text-[#80868b]">
            Illustrative figures, not a real account
          </p>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;

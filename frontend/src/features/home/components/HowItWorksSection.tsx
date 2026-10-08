import { STEPS } from "../../../shared/constants/homeData";

const HowItWorksSection: React.FC = () => (
  <section id="how-it-works" className="bg-white px-5 py-16 sm:py-20">
    <div className="mx-auto grid max-w-6xl items-center gap-10 lg:grid-cols-2 lg:gap-16">
      {/* Content */}
      <div>
        <p className="mb-3 text-sm font-medium text-[#1a73e8]">How it works</p>

        <h2 className="max-w-lg text-3xl font-semibold tracking-tight text-[#202124] sm:text-4xl">
          A clearer picture, one step at a time.
        </h2>

        <p className="mt-4 max-w-lg text-base leading-7 text-[#5f6368]">
          Add your transactions, set goals, and get useful answers about your
          money.
        </p>

        <ol className="mt-8 space-y-5">
          {STEPS.map((step) => (
            <li key={step.number} className="flex gap-3.5">
              <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-[#e8f0fe] text-xs font-medium text-[#1967d2]">
                {step.number}
              </span>

              <div className="pt-0.5">
                <h3 className="text-sm font-medium text-[#202124]">
                  {step.title}
                </h3>

                <p className="mt-1 text-sm leading-6 text-[#5f6368]">
                  {step.description}
                </p>
              </div>
            </li>
          ))}
        </ol>
      </div>

      {/* Simple chat preview */}
      <aside
        aria-label="Example conversation with fin"
        className="overflow-hidden rounded-2xl border border-[#e8eaed] bg-white shadow-[0_6px_24px_rgba(60,64,67,0.07)]"
      >
        {/* Header */}
        <div className="flex items-center gap-3 border-b border-[#e8eaed] px-5 py-4">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-[#063fed] text-xs font-semibold text-white">
            f
          </div>

          <div>
            <p className="text-sm font-medium text-[#202124]">fin</p>
            <p className="text-xs text-[#80868b]">Your finance assistant</p>
          </div>
        </div>

        {/* Messages */}
        <div className="space-y-4 bg-[#f8fafd] p-5 sm:p-6">
          {/* User */}
          <div className="flex justify-end">
            <p className="max-w-[82%] rounded-2xl rounded-br-md bg-white px-4 py-3 text-sm leading-6 text-black">
              How much is left in my food budget?
            </p>
          </div>

          {/* Assistant */}
          <div className="max-w-[90%] rounded-2xl rounded-bl-md border border-[#e8eaed] bg-white px-4 py-4">
            <p className="text-sm leading-6 text-[#3c4043]">
              You have{" "}
              <span className="font-semibold text-[#202124]">₹1,760</span> left
              from your ₹8,000 food budget this month.
            </p>

            <p className="mt-3 border-t border-[#f1f3f4] pt-3 text-xs text-[#80868b]">
              Based on your recorded transactions and budget
            </p>
          </div>

          {/* Simple follow-up */}
          <div className="flex flex-wrap gap-2 pt-1">
            <span className="max-w-full rounded-full border border-[#dadce0] bg-white px-3 py-1.5 text-xs text-[#5f6368]">
              Where did I spend the most?
            </span>

            <span className="max-w-full rounded-full border border-[#dadce0] bg-white px-3 py-1.5 text-xs text-[#5f6368]">
              Am I on track?
            </span>
          </div>
        </div>
      </aside>
    </div>
  </section>
);

export default HowItWorksSection;

import { STEPS } from "../../../shared/constants/homeData";

const HowItWorksSection: React.FC = () => (
  <section id="how-it-works" className="bg-white px-5 py-16 sm:py-20">
    <div className="mx-auto grid max-w-6xl items-center gap-10 lg:grid-cols-2 lg:gap-16">
      <div>
        <p className="mb-3 text-sm font-medium text-[#1a73e8]">
          A routine that works
        </p>
        <h2 className="max-w-lg text-3xl font-semibold tracking-tight text-[#202124] sm:text-4xl">
          Small steps. A better view of your finances.
        </h2>
        <p className="mt-4 max-w-lg text-base leading-7 text-[#5f6368]">
          Get started in a few minutes, then build a money routine that fits
          your life.
        </p>

        <ol className="mt-8 space-y-6">
          {STEPS.map((step) => (
            <li key={step.number} className="flex gap-4">
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

      <div className="overflow-hidden rounded-2xl border border-[#e8eaed] bg-white shadow-[0_8px_32px_rgba(60,64,67,0.08)]">
        <div className="flex items-center justify-between border-b border-[#e8eaed] px-5 py-4">
          <div>
            <p className="text-sm font-medium text-[#202124]">
              Ask about your finances
            </p>
            <p className="mt-0.5 text-xs text-[#5f6368]">
              Example conversation
            </p>
          </div>
          <span className="flex h-8 w-8 items-center justify-center rounded-full bg-[#e8f0fe] text-sm font-medium text-[#1967d2]">
            f
          </span>
        </div>

        <div className="space-y-5 bg-[#f8fafd] p-5 sm:p-6">
          <div className="ml-auto max-w-[85%] rounded-2xl rounded-br-md bg-[#e8f0fe] px-4 py-3 text-sm leading-6 text-[#202124]">
            How much do I have left in my food budget?
          </div>
          <div className="max-w-[92%] rounded-2xl rounded-bl-md border border-[#e8eaed] bg-white px-4 py-4 text-sm leading-6 text-[#3c4043]">
            You’ve spent ₹6,240 of your ₹8,000 food budget this month, leaving
            ₹1,760.
            <div className="mt-3 border-t border-[#f1f3f4] pt-3 text-xs text-[#5f6368]">
              Based on your recorded transactions and budget
            </div>
          </div>
          <p className="text-center text-xs text-[#80868b]">
            fin’s AI uses your financial data to provide relevant insights.
          </p>
        </div>
      </div>
    </div>
  </section>
);

export default HowItWorksSection;

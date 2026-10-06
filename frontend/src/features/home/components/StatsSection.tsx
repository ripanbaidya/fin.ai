const HIGHLIGHTS = [
  "Understand your spending",
  "Build practical budgets",
  "Keep savings goals in view",
  "Get insights from your data",
];

const StatsSection: React.FC = () => (
  <section className="border-y border-[#e8eaed] bg-white py-7">
    <div className="mx-auto grid max-w-6xl grid-cols-2 gap-x-5 gap-y-4 px-5 sm:grid-cols-4 sm:gap-6">
      {HIGHLIGHTS.map((highlight) => (
        <div key={highlight} className="flex items-center gap-2.5">
          <span
            aria-hidden="true"
            className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-[#e8f0fe] text-[#1a73e8]"
          >
            <svg viewBox="0 0 16 16" fill="none" className="h-3 w-3">
              <path
                d="m3.5 8 3 3 6-6"
                stroke="currentColor"
                strokeWidth="1.6"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </span>
          <span className="text-xs leading-5 text-[#5f6368] sm:text-sm">
            {highlight}
          </span>
        </div>
      ))}
    </div>
  </section>
);

export default StatsSection;

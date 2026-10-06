interface Props {
  month: string; // yyyy-MM
  onChange: (month: string) => void;
}

// Parse "yyyy-MM" → { year, month }
const parseMonth = (m: string) => {
  const [year, month] = m.split("-").map(Number);
  return { year, month };
};

// Format → "March 2026"
const formatDisplay = (m: string) => {
  const { year, month } = parseMonth(m);
  return new Date(year, month - 1, 1).toLocaleDateString("en-IN", {
    month: "long",
    year: "numeric",
  });
};

// Shift month by delta (-1, +1)
const shiftMonth = (m: string, delta: number): string => {
  const { year, month } = parseMonth(m);
  const d = new Date(year, month - 1 + delta, 1);
  const y = d.getFullYear();
  const mo = String(d.getMonth() + 1).padStart(2, "0");
  return `${y}-${mo}`;
};

// Current month helper
const currentMonth = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
};

const MonthNavigator: React.FC<Props> = ({ month, onChange }) => {
  const isCurrentMonth = month === currentMonth();

  return (
    // flex-wrap allows safe wrapping on very small screens
    <div className="flex items-center gap-2 flex-wrap sm:flex-nowrap">
      {/* Prev Button */}
      <button
        onClick={() => onChange(shiftMonth(month, -1))}
        className="p-1.5 rounded-lg border border-gray-200 text-gray-500 hover:bg-gray-50 hover:text-gray-800 transition-colors text-sm leading-none flex-shrink-0"
        aria-label="Previous month"
      >
        ←
      </button>

      {/* Month Label */}
      <span
        className="
          text-sm font-medium text-gray-800 text-center
          min-w-[120px] sm:min-w-[140px]   /* slightly flexible on mobile */
          truncate                        /* prevents overflow */
        "
      >
        {formatDisplay(month)}
      </span>

      {/* Next Button */}
      <button
        onClick={() => onChange(shiftMonth(month, 1))}
        disabled={isCurrentMonth}
        className="p-1.5 rounded-lg border border-gray-200 text-gray-500 hover:bg-gray-50 hover:text-gray-800 disabled:opacity-30 disabled:cursor-not-allowed transition-colors text-sm leading-none flex-shrink-0"
        aria-label="Next month"
      >
        →
      </button>

      {/* Jump to Today */}
      {!isCurrentMonth && (
        <button
          onClick={() => onChange(currentMonth())}
          className="
            text-xs text-gray-400 hover:text-black underline underline-offset-2 transition-colors
            ml-1 flex-shrink-0 whitespace-nowrap
          "
        >
          Today
        </button>
      )}
    </div>
  );
};

export default MonthNavigator;

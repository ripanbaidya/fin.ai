interface Props {
  month: string; // yyyy-MM
  onChange: (month: string) => void;
}

const parseMonth = (m: string) => {
  const [year, month] = m.split("-").map(Number);
  return { year, month };
};

const formatDisplay = (m: string) => {
  const { year, month } = parseMonth(m);
  return new Date(year, month - 1, 1).toLocaleDateString("en-IN", {
    month: "long",
    year: "numeric",
  });
};

const shiftMonth = (m: string, delta: number): string => {
  const { year, month } = parseMonth(m);
  const d = new Date(year, month - 1 + delta, 1);
  const y = d.getFullYear();
  const mo = String(d.getMonth() + 1).padStart(2, "0");
  return `${y}-${mo}`;
};

const currentMonth = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
};

const BudgetMonthNav: React.FC<Props> = ({ month, onChange }) => {
  const isCurrentMonth = month === currentMonth();

  return (
    <div className="flex items-center gap-2">
      <button
        onClick={() => onChange(shiftMonth(month, -1))}
        className="p-1.5 rounded-lg border border-gray-200 text-gray-500 hover:bg-gray-50 transition-colors text-sm leading-none"
      >
        ←
      </button>
      <span className="text-sm font-medium text-gray-800 min-w-[140px] text-center">
        {formatDisplay(month)}
      </span>
      <button
        onClick={() => onChange(shiftMonth(month, 1))}
        disabled={isCurrentMonth}
        className="p-1.5 rounded-lg border border-gray-200 text-gray-500 hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed transition-colors text-sm leading-none"
      >
        →
      </button>
      {!isCurrentMonth && (
        <button
          onClick={() => onChange(currentMonth())}
          className="text-xs text-gray-400 hover:text-black underline underline-offset-2 transition-colors ml-1"
        >
          Today
        </button>
      )}
    </div>
  );
};

export default BudgetMonthNav;

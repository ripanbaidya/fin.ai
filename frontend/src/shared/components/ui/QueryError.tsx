import { AppError } from "../../../api/errorParser";
import clsx from "clsx";

interface Props {
  error: AppError | null;
  onRetry?: () => void;
  className?: string;
}

export function QueryError({ error, onRetry, className }: Props) {
  if (!error) return null;

  return (
    <div
      role="alert"
      className={clsx(
        "flex flex-col items-center justify-center text-center",
        "px-4 py-8 sm:py-10 md:py-12",
        "gap-3 sm:gap-4",
        "text-gray-600",
        className,
      )}
    >
      {/* Message */}
      <p className="text-sm sm:text-base max-w-xs sm:max-w-md break-words">
        {error.message || "Something went wrong. Please try again."}
      </p>

      {/* Retry Button */}
      {onRetry && (
        <button
          onClick={onRetry}
          className={clsx(
            "mt-1",
            "text-sm sm:text-base font-medium",
            "text-blue-600 hover:text-blue-800",
            "underline",
            "px-3 py-1.5 sm:px-4 sm:py-2",
            "rounded-md",
            "focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2",
          )}
        >
          Try again
        </button>
      )}
    </div>
  );
}

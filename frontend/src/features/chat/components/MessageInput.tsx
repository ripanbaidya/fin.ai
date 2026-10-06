import { useState, useRef, useEffect } from "react";

interface Props {
  onSend: (question: string) => void;
  isDisabled: boolean;
}

const SUGGESTIONS = [
  "How much did I spend this month?",
  "What are my top expense categories?",
  "Am I on track with my savings goals?",
  "Summarise my finances this week",
];

const MessageInput: React.FC<Props> = ({ onSend, isDisabled }) => {
  const [value, setValue] = useState("");
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const el = textareaRef.current;
    if (!el) return;
    el.style.height = "auto";
    el.style.height = `${Math.min(el.scrollHeight, 128)}px`;
  }, [value]);

  const handleSend = () => {
    const trimmed = value.trim();
    if (!trimmed || isDisabled) return;
    onSend(trimmed);
    setValue("");
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="shrink-0 bg-white border-t border-gray-100 px-3 sm:px-4 pt-3 pb-[calc(1rem_+_env(safe-area-inset-bottom))]">
      {/* Suggestion chips — horizontally scrollable on mobile */}
      {!value && !isDisabled && (
        <div className="flex gap-2 mb-3 overflow-x-auto scrollbar-hide pb-0.5 -mx-3 sm:mx-0 px-3 sm:px-0 sm:flex-wrap">
          {SUGGESTIONS.map((s) => (
            <button
              key={s}
              onClick={() => {
                setValue(s);
                textareaRef.current?.focus();
              }}
              className="text-xs text-gray-500 bg-gray-50 border border-gray-200 rounded-full px-3 py-1.5 hover:bg-gray-100 hover:text-gray-700 hover:border-gray-300 transition-all whitespace-nowrap shrink-0 sm:shrink sm:whitespace-normal"
            >
              {s}
            </button>
          ))}
        </div>
      )}

      {/* Input box */}
      <div
        className={`flex items-end gap-2 bg-gray-50 border rounded-2xl px-3 sm:px-4 py-2.5 transition-all ${
          isDisabled
            ? "border-gray-100"
            : "border-gray-200 focus-within:border-gray-400 focus-within:bg-white focus-within:shadow-sm"
        }`}
      >
        <textarea
          ref={textareaRef}
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={isDisabled}
          placeholder={
            isDisabled
              ? "fin.ai is thinking..."
              : "Ask about your finances..."
          }
          rows={1}
          className="flex-1 resize-none bg-transparent text-sm text-gray-800 placeholder-gray-400 outline-none leading-relaxed disabled:text-gray-400 py-0.5"
        />

        {/* Send button */}
        <button
          onClick={handleSend}
          disabled={isDisabled || !value.trim()}
          className={`shrink-0 w-8 h-8 rounded-xl flex items-center justify-center transition-all ${
            value.trim() && !isDisabled
              ? "bg-gray-900 text-white hover:bg-black shadow-sm"
              : "bg-gray-200 text-gray-400 cursor-not-allowed"
          }`}
        >
          {isDisabled ? (
            <span className="w-3.5 h-3.5 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
          ) : (
            <svg
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <line x1="22" y1="2" x2="11" y2="13" />
              <polygon points="22 2 15 22 11 13 2 9 22 2" />
            </svg>
          )}
        </button>
      </div>

      {/* Hint — hidden on very small screens to save space */}
      <p className="hidden sm:block text-xs text-gray-400 text-center mt-2">
        Enter to send · Shift + Enter for new line
      </p>
    </div>
  );
};

export default MessageInput;

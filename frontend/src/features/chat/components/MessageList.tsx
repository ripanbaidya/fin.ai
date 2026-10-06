import { useEffect, useRef } from "react";
import type { ChatMessageResponse } from "../chat.types";

interface Props {
  messages: ChatMessageResponse[];
  isQuerying: boolean;
}

const formatTime = (dateStr: string) =>
  new Date(dateStr).toLocaleTimeString("en-IN", {
    hour: "2-digit",
    minute: "2-digit",
  });

// Markdown-lite renderer

const renderInline = (text: string) => {
  const parts = text.split(/(\*\*[^*]+\*\*)/g);
  return parts.map((part, i) => {
    if (part.startsWith("**") && part.endsWith("**")) {
      return (
        <strong key={i} className="font-semibold">
          {part.slice(2, -2)}
        </strong>
      );
    }
    return <span key={i}>{part}</span>;
  });
};

const renderContent = (content: string) => {
  const lines = content.split("\n");
  const elements: React.ReactNode[] = [];
  let bulletBuffer: string[] = [];

  const flushBullets = (key: number) => {
    if (bulletBuffer.length === 0) return;
    elements.push(
      <ul key={`ul-${key}`} className="space-y-0.5 my-1 pl-1">
        {bulletBuffer.map((line, i) => (
          <li key={i} className="flex items-start gap-2">
            <span className="mt-1.5 w-1 h-1 rounded-full bg-gray-400 shrink-0" />
            <span>{renderInline(line)}</span>
          </li>
        ))}
      </ul>,
    );
    bulletBuffer = [];
  };

  lines.forEach((line, i) => {
    if (line.startsWith("- ") || line.startsWith("• ")) {
      bulletBuffer.push(line.replace(/^[-•]\s/, ""));
    } else {
      flushBullets(i);
      if (line.trim() === "") {
        elements.push(<div key={i} className="h-1.5" />);
      } else {
        elements.push(<p key={i}>{renderInline(line)}</p>);
      }
    }
  });
  flushBullets(lines.length);

  return elements;
};

// Typing indicator

const TypingIndicator = () => (
  <div className="flex items-end gap-2 sm:gap-3 px-3 sm:px-4">
    <div className="w-7 h-7 sm:w-8 sm:h-8 rounded-full bg-gradient-to-br from-gray-800 to-gray-600 flex items-center justify-center shrink-0 shadow-sm">
      <span className="text-white text-xs font-bold">F</span>
    </div>
    <div className="bg-white border border-gray-200 rounded-2xl rounded-bl-sm px-4 py-3 shadow-sm">
      <div className="flex gap-1.5 items-center h-4">
        {[0, 1, 2].map((i) => (
          <span
            key={i}
            className="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce"
            style={{ animationDelay: `${i * 0.18}s` }}
          />
        ))}
      </div>
    </div>
  </div>
);

// Empty state

const EmptyState = () => (
  <div className="flex-1 flex flex-col items-center justify-center px-6 sm:px-8 py-12 text-center">
    <div className="w-14 h-14 sm:w-16 sm:h-16 rounded-2xl bg-gradient-to-br from-gray-900 to-gray-700 flex items-center justify-center mb-4 shadow-lg">
      <span className="text-white text-xl sm:text-2xl font-bold">F</span>
    </div>
    <h3 className="text-sm font-semibold text-gray-800 mb-2">
      Ask fin.ai anything
    </h3>
    <p className="text-xs text-gray-400 max-w-[260px] leading-relaxed">
      Get insights on your spending, budget status, savings progress, and
      personalised financial advice — all from your real data.
    </p>

    <div className="mt-6 grid grid-cols-2 gap-2 w-full max-w-sm text-left">
      {[
        { icon: "💸", text: "How much did I spend this month?" },
        { icon: "📊", text: "What are my top expense categories?" },
        { icon: "🎯", text: "Am I on track with savings goals?" },
        { icon: "📈", text: "Show my spending trend this week" },
      ].map(({ icon, text }) => (
        <div
          key={text}
          className="flex items-start gap-2 p-2.5 rounded-xl bg-gray-50 border border-gray-100"
        >
          <span className="text-base shrink-0">{icon}</span>
          <p className="text-xs text-gray-500 leading-snug">{text}</p>
        </div>
      ))}
    </div>
  </div>
);

// Main component

const MessageList: React.FC<Props> = ({ messages, isQuerying }) => {
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isQuerying]);

  if (messages.length === 0 && !isQuerying) {
    return <EmptyState />;
  }

  return (
    <div className="flex-1 overflow-y-auto py-5 space-y-5">
      {messages.map((msg) => {
        const isUser = msg.role === "USER";

        return (
          <div
            key={msg.id}
            className={`flex items-end gap-2 sm:gap-3 px-3 sm:px-4 ${
              isUser ? "flex-row-reverse" : ""
            }`}
          >
            {/* Avatar */}
            {isUser ? (
              <div className="w-7 h-7 sm:w-8 sm:h-8 rounded-full bg-black flex items-center justify-center shrink-0 shadow-sm">
                <span className="text-white text-xs font-bold">U</span>
              </div>
            ) : (
              <div className="w-7 h-7 sm:w-8 sm:h-8 rounded-full bg-gradient-to-br from-gray-800 to-gray-600 flex items-center justify-center shrink-0 shadow-sm">
                <span className="text-white text-xs font-bold">F</span>
              </div>
            )}

            {/* Bubble — wider on mobile since there's no sidebar */}
            <div
              className={`max-w-[82%] sm:max-w-[72%] space-y-1 flex flex-col ${
                isUser ? "items-end" : "items-start"
              }`}
            >
              <div
                className={`rounded-2xl px-3 sm:px-4 py-2.5 sm:py-3 text-sm leading-relaxed shadow-sm ${
                  isUser
                    ? "bg-gray-900 text-white rounded-br-sm"
                    : "bg-white border border-gray-200 text-gray-800 rounded-bl-sm"
                }`}
              >
                {isUser ? (
                  <p className="whitespace-pre-wrap">{msg.content}</p>
                ) : (
                  <div className="space-y-1 text-sm leading-relaxed">
                    {renderContent(msg.content)}
                  </div>
                )}
              </div>

              {/* Timestamp */}
              <p className="text-xs text-gray-400 px-1">
                {formatTime(msg.createdAt)}
              </p>
            </div>
          </div>
        );
      })}

      {isQuerying && <TypingIndicator />}
      <div ref={bottomRef} />
    </div>
  );
};

export default MessageList;

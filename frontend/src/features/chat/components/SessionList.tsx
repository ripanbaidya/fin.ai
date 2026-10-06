import type { ChatSessionResponse } from "../chat.types";

interface Props {
  sessions: ChatSessionResponse[];
  activeSessionId: string | null;
  onSelect: (session: ChatSessionResponse) => void;
  onCreate: () => void;
  onDelete: (id: string) => void;
  isCreating: boolean;
  isDeleting: boolean;
}

const formatDate = (isoStr: string) => {
  const d = new Date(isoStr);
  const now = new Date();
  const diffMs = now.getTime() - d.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return "Just now";
  if (diffMins < 60) return `${diffMins}m ago`;
  if (diffHours < 24) return `${diffHours}h ago`;
  if (diffDays === 1) return "Yesterday";
  if (diffDays < 7) return `${diffDays}d ago`;
  return d.toLocaleDateString("en-IN", { day: "numeric", month: "short" });
};

const SessionList: React.FC<Props> = ({
  sessions,
  activeSessionId,
  onSelect,
  onCreate,
  onDelete,
  isCreating,
  isDeleting,
}) => {
  return (
    <div className="flex flex-col h-full">
      {/* ── Header ── */}
      <div className="px-4 pt-5 pb-4">
        <div className="flex items-center justify-between mb-4">
          <p className="text-xs font-semibold uppercase tracking-widest text-gray-400">
            Conversations
          </p>
          <button
            onClick={onCreate}
            disabled={isCreating}
            title="New chat"
            className="w-7 h-7 flex items-center justify-center rounded-lg bg-gray-900 text-white hover:bg-gray-700 disabled:opacity-50 transition-colors text-base leading-none"
          >
            {isCreating ? (
              <span className="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <span className="mb-0.5">+</span>
            )}
          </button>
        </div>

        {/* New chat shortcut */}
        <button
          onClick={onCreate}
          disabled={isCreating}
          className="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl border border-dashed border-gray-200 text-gray-400 hover:border-gray-400 hover:text-gray-600 hover:bg-gray-50 transition-all text-xs disabled:opacity-50"
        >
          <svg
            width="13"
            height="13"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
          </svg>
          New conversation
        </button>
      </div>

      {/* ── Session list ── */}
      <div className="flex-1 overflow-y-auto px-2 pb-4 space-y-0.5">
        {sessions.length === 0 ? (
          <div className="px-3 py-10 text-center">
            <p className="text-xs text-gray-400">No conversations yet.</p>
          </div>
        ) : (
          sessions.map((session) => {
            const isActive = session.id === activeSessionId;
            return (
              <div
                key={session.id}
                onClick={() => onSelect(session)}
                className={`group relative flex items-start gap-2.5 px-3 py-2.5 rounded-xl cursor-pointer transition-all ${
                  isActive
                    ? "bg-gray-900 text-white"
                    : "text-gray-700 hover:bg-gray-100"
                }`}
              >
                {/* Icon */}
                <div className="mt-0.5 shrink-0 text-gray-400">
                  <svg
                    width="13"
                    height="13"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                  </svg>
                </div>

                {/* Text */}
                <div className="min-w-0 flex-1">
                  <p
                    className={`text-xs font-medium truncate leading-snug ${
                      isActive ? "text-white" : "text-gray-800"
                    }`}
                  >
                    {session.title}
                  </p>
                  <p
                    className={`text-xs mt-0.5 ${
                      isActive ? "text-gray-400" : "text-gray-400"
                    }`}
                  >
                    {formatDate(session.updatedAt)}
                  </p>
                </div>

                {/* Delete */}
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onDelete(session.id);
                  }}
                  disabled={isDeleting}
                  className={`shrink-0 w-5 h-5 flex items-center justify-center rounded opacity-0 group-hover:opacity-100 transition-opacity disabled:opacity-20 mt-0.5 ${
                    isActive
                      ? "text-gray-400 hover:text-red-400"
                      : "text-gray-400 hover:text-red-500"
                  }`}
                >
                  <svg
                    width="11"
                    height="11"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.5"
                    strokeLinecap="round"
                  >
                    <line x1="18" y1="6" x2="6" y2="18" />
                    <line x1="6" y1="6" x2="18" y2="18" />
                  </svg>
                </button>
              </div>
            );
          })
        )}
      </div>

      {/* ── Footer branding ── */}
      <div className="px-4 py-3 border-t border-gray-100">
        <p className="text-xs text-gray-400 text-center">
          Powered by{" "}
          <span className="font-medium text-gray-600">fin.ai AI</span>
        </p>
      </div>
    </div>
  );
};

export default SessionList;

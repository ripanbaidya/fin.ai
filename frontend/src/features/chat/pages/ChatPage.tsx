import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { chatService } from "../chatService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import Spinner from "../../../shared/components/ui/Spinner";
import { QueryError } from "../../../shared/components/ui/QueryError";

import SessionList from "../components/SessionList";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";

import type { ChatMessageResponse, ChatSessionResponse } from "../chat.types";
import type { ResponseWrapper } from "../../../types/api.types";

export default function ChatPage() {
  const queryClient = useQueryClient();

  const [activeSession, setActiveSession] =
    useState<ChatSessionResponse | null>(null);
  const [isQuerying, setIsQuerying] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const sessionsQuery = useAppQuery({
    queryKey: ["chat-sessions"],
    queryFn: () => chatService.getSessions(),
  });

  const messagesQuery = useAppQuery<ResponseWrapper<ChatMessageResponse[]>>({
    queryKey: ["chat-messages", activeSession?.id],
    queryFn: () => chatService.getMessages(activeSession!.id),
    enabled: !!activeSession,
  });

  const appendMessage = (sessionId: string, message: ChatMessageResponse) => {
    queryClient.setQueryData<ResponseWrapper<ChatMessageResponse[]>>(
      ["chat-messages", sessionId],
      (current) =>
        current
          ? { ...current, data: [...current.data, message] }
          : current,
    );
  };

  const { mutate: createSession, isPending: isCreatingSession } =
    useAppMutation({
      mutationFn: () => chatService.createSession({}),
      onSuccess: (res) => {
        queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
        setActiveSession(res.data);
        setSidebarOpen(false);
      },
      onError: (err: AppError) =>
        console.error("Create session failed", err.message),
    });

  const { mutate: deleteSession, isPending: isDeletingSession } =
    useAppMutation({
      mutationFn: (id: string) => chatService.deleteSession(id),
      onSuccess: (_, deletedId) => {
        queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
        if (activeSession?.id === deletedId) {
          setActiveSession(null);
        }
      },
      onError: (err: AppError) =>
        console.error("Delete session failed", err.message),
    });

  const { mutate: sendQuery } = useAppMutation({
    mutationFn: ({
      sessionId,
      question,
    }: {
      sessionId: string;
      question: string;
    }) => chatService.query(sessionId, { question }),

    onMutate: async ({ sessionId, question }) => {
      const queryKey = ["chat-messages", sessionId];
      await queryClient.cancelQueries({ queryKey });
      const userMsg: ChatMessageResponse = {
        id: `temp-user-${Date.now()}`,
        role: "USER",
        content: question,
        createdAt: new Date().toISOString(),
      };
      appendMessage(sessionId, userMsg);
      setIsQuerying(true);
    },

    onSuccess: (res, variables) => {
      const assistantMsg: ChatMessageResponse = {
        id: `temp-assistant-${Date.now()}`,
        role: "ASSISTANT",
        content: res.data.answer,
        createdAt: new Date().toISOString(),
      };
      appendMessage(variables.sessionId, assistantMsg);
      if (activeSession?.id === variables.sessionId) setIsQuerying(false);
      queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
      queryClient.invalidateQueries({
        queryKey: ["chat-messages", variables.sessionId],
      });
    },

    onError: (err: AppError, variables) => {
      if (activeSession?.id === variables.sessionId) setIsQuerying(false);
      const errMsg: ChatMessageResponse = {
        id: `temp-error-${Date.now()}`,
        role: "ASSISTANT",
        content: `Something went wrong: ${err.message}. Please try again.`,
        createdAt: new Date().toISOString(),
      };
      appendMessage(variables.sessionId, errMsg);
    },
  });

  // ── Derived values ──
  const sessions = sessionsQuery.data?.data ?? [];
  const messages = messagesQuery.data?.data ?? [];

  // ── Handlers ──
  const handleSelectSession = (session: ChatSessionResponse) => {
    if (session.id === activeSession?.id) return;
    setActiveSession(session);
    setIsQuerying(false);
    setSidebarOpen(false);
  };

  const handleSend = (question: string) => {
    if (!activeSession || isQuerying) return;
    sendQuery({ sessionId: activeSession.id, question });
  };

  // ── Sidebar content ──
  const sidebarContent = sessionsQuery.isLoading ? (
    <div className="flex justify-center py-10">
      <Spinner />
    </div>
  ) : sessionsQuery.error ? (
    <div className="p-4">
      <QueryError
        error={sessionsQuery.error}
        onRetry={() => sessionsQuery.refetch()}
      />
    </div>
  ) : (
    <SessionList
      sessions={sessions}
      activeSessionId={activeSession?.id ?? null}
      onSelect={handleSelectSession}
      onCreate={() => createSession(undefined)}
      onDelete={(id) => deleteSession(id)}
      isCreating={isCreatingSession}
      isDeleting={isDeletingSession}
    />
  );

  // ── Normal chat render ──
  return (
    <div className="relative flex h-[calc(100dvh_-_9rem)] min-h-0 w-full min-w-0 overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm md:h-[calc(100vh_-_5rem)]">
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/30 z-20 md:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      <div
        className={`
          fixed top-0 left-0 h-full w-64 max-w-[calc(100vw-2rem)] bg-gray-50 border-r border-gray-200 z-30 flex flex-col
          transition-transform duration-300 ease-in-out
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
          md:static md:w-60 md:shrink-0 md:translate-x-0 md:z-auto md:transition-none
        `}
      >
        {sidebarContent}
      </div>

      <div className="flex-1 flex flex-col min-w-0 bg-gray-50/30">
        {!activeSession ? (
          <div className="flex-1 flex flex-col items-center justify-center text-center px-6 sm:px-8">
            <button
              onClick={() => setSidebarOpen(true)}
              className="md:hidden absolute top-4 left-4 w-8 h-8 flex items-center justify-center rounded-lg text-gray-500 hover:bg-gray-100 transition-colors"
              aria-label="Open conversations"
            >
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
              >
                <line x1="3" y1="6" x2="21" y2="6" />
                <line x1="3" y1="12" x2="21" y2="12" />
                <line x1="3" y1="18" x2="21" y2="18" />
              </svg>
            </button>

            <div className="w-14 h-14 sm:w-16 sm:h-16 rounded-2xl bg-gradient-to-br from-gray-900 to-gray-700 flex items-center justify-center mb-5 shadow-lg">
              <span className="text-white text-xl sm:text-2xl font-bold">
                F
              </span>
            </div>
            <h2 className="text-base font-semibold text-gray-900 mb-1.5">
              fin.ai AI Assistant
            </h2>
            <p className="text-xs text-gray-400 max-w-xs leading-relaxed mb-5">
              Powered by RAG — every answer is grounded in your actual
              transaction data, not generic advice.
            </p>
            <button
              onClick={() => createSession(undefined)}
              disabled={isCreatingSession}
              className="flex items-center gap-2 text-sm bg-gray-900 text-white px-5 py-2.5 rounded-xl hover:bg-black disabled:opacity-60 transition-colors shadow-sm"
            >
              {isCreatingSession ? (
                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <svg
                  width="14"
                  height="14"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              )}
              {isCreatingSession ? "Starting..." : "Start a conversation"}
            </button>

            <div className="mt-8 grid w-full max-w-sm grid-cols-1 gap-2 text-xs text-gray-500 min-[400px]:grid-cols-3 sm:gap-3">
              {[
                { icon: "🔍", label: "Semantic search over your transactions" },
                { icon: "💡", label: "Budget & savings insights" },
                { icon: "📊", label: "Spending patterns & trends" },
              ].map(({ icon, label }) => (
                <div
                  key={label}
                  className="flex flex-col items-center gap-1.5 p-2.5 sm:p-3 rounded-xl bg-white border border-gray-100 text-center"
                >
                  <span className="text-xl">{icon}</span>
                  <span className="leading-snug text-gray-400">{label}</span>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <>
            <div className="shrink-0 flex items-center gap-3 px-3 sm:px-5 py-3.5 bg-white border-b border-gray-100 shadow-sm">
              <button
                onClick={() => setSidebarOpen(true)}
                className="md:hidden w-8 h-8 flex items-center justify-center rounded-lg text-gray-500 hover:bg-gray-100 transition-colors shrink-0"
                aria-label="Open conversations"
              >
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                >
                  <line x1="3" y1="6" x2="21" y2="6" />
                  <line x1="3" y1="12" x2="21" y2="12" />
                  <line x1="3" y1="18" x2="21" y2="18" />
                </svg>
              </button>

              <div className="min-w-0 flex-1">
                <p className="text-sm font-semibold text-gray-900 truncate">
                  {activeSession.title}
                </p>
                <p className="text-xs text-gray-400 mt-0.5">
                  {messages.length} message{messages.length !== 1 ? "s" : ""}
                  {isQuerying && (
                    <span className="ml-2 text-blue-500 font-medium">
                      · Thinking...
                    </span>
                  )}
                </p>
              </div>

              <div className="shrink-0">
                <button
                  onClick={() => createSession(undefined)}
                  disabled={isCreatingSession}
                  title="New chat"
                  className="text-xs text-gray-500 border border-gray-200 px-3 py-1.5 rounded-lg hover:bg-gray-50 disabled:opacity-40 transition-colors flex items-center gap-1.5"
                >
                  <svg
                    width="11"
                    height="11"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <line x1="12" y1="5" x2="12" y2="19" />
                    <line x1="5" y1="12" x2="19" y2="12" />
                  </svg>
                  <span className="hidden sm:inline">New chat</span>
                </button>
              </div>
            </div>

            {messagesQuery.isLoading && messages.length === 0 ? (
              <div className="flex-1 flex items-center justify-center">
                <Spinner />
              </div>
            ) : (
              <MessageList
                messages={messagesQuery.data?.data ?? []}
                isQuerying={isQuerying}
              />
            )}

            <MessageInput
              onSend={handleSend}
              isDisabled={
                isQuerying || messagesQuery.isLoading || !!messagesQuery.error
              }
            />
          </>
        )}
      </div>
    </div>
  );
}

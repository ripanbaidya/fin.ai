export type MessageRole = 'USER' | 'ASSISTANT';

export interface ChatSessionResponse {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface ChatMessageResponse {
  id: string;
  role: MessageRole;
  content: string;
  createdAt: string;
}

export interface CreateSessionRequest {
  title?: string;
}

export interface ChatQueryRequest {
  question: string;
}

export interface ChatQueryResponse {
  answer: string;
}
import { apiClient } from '../../lib/axios';
import type { ResponseWrapper } from '../../types/api.types';
import type {
  ChatSessionResponse,
  ChatMessageResponse,
  CreateSessionRequest,
  ChatQueryRequest,
  ChatQueryResponse,
} from './chat.types';

export const chatService = {
  getSessions: async (): Promise<ResponseWrapper<ChatSessionResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<ChatSessionResponse[]>>('/chat/sessions');
    return res.data;
  },

  createSession: async (data: CreateSessionRequest): Promise<ResponseWrapper<ChatSessionResponse>> => {
    const res = await apiClient.post<ResponseWrapper<ChatSessionResponse>>('/chat/sessions', data);
    return res.data;
  },

  deleteSession: async (id: string): Promise<void> => {
    await apiClient.delete<void>(`/chat/sessions/${id}`);
  },

  getMessages: async (sessionId: string): Promise<ResponseWrapper<ChatMessageResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<ChatMessageResponse[]>>(`/chat/sessions/${sessionId}/messages`);
    return res.data;
  },

  query: async (sessionId: string, data: ChatQueryRequest): Promise<ResponseWrapper<ChatQueryResponse>> => {
    const res = await apiClient.post<ResponseWrapper<ChatQueryResponse>>(`/chat/sessions/${sessionId}/query`, data);
    return res.data;
  },
};
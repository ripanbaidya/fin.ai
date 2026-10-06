import { apiClient } from "../../lib/axios";
import type { ResponseWrapper } from "../../types/api.types";
import type { CategoryResponse, CreateCategoryRequest, UpdateCategoryRequest } from "./category.types";

export const categoryService = {
  getAll: async (type?: 'INCOME' | 'EXPENSE'): Promise<ResponseWrapper<CategoryResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<CategoryResponse[]>>('/categories', { params: { type } });
    return res.data;
  },

  create: async (data: CreateCategoryRequest): Promise<ResponseWrapper<CategoryResponse>> => {
    const res = await apiClient.post<ResponseWrapper<CategoryResponse>>('/categories', data);
    return res.data
  },

  update: async (id: string, data: UpdateCategoryRequest): Promise<ResponseWrapper<CategoryResponse>> => {
    const res = await apiClient.put<ResponseWrapper<CategoryResponse>>(`/categories/${id}`, data);
    return res.data
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete<void>(`/categories/${id}`);
  }
}
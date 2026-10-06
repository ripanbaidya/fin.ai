export type CategoryType = 'INCOME' | 'EXPENSE';

export interface CategoryResponse {
    id: string;
    name: string;
    categoryType: CategoryType;
    isDefault: boolean;
}

export interface CreateCategoryRequest {
    name: string;
    categoryType: CategoryType;
}

export interface UpdateCategoryRequest {
    name: string;
    categoryType: CategoryType;
}
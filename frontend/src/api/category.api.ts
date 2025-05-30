import api from './config';

export interface Category {
  id: number;
  name: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export const categoryApi = {
  getCategories: async () => {
    const response = await api.get<Category[]>('/api/categories');
    return response.data;
  },

  addCategory: async (category: Omit<Category, 'id'>) => {
    const response = await api.post<ApiResponse<Category>>('/api/categories', category);
    return response.data;
  },

  updateCategory: async (id: number, category: Omit<Category, 'id'>) => {
    const response = await api.put<ApiResponse<Category>>(`/api/categories/${id}`, category);
    return response.data;
  },

  deleteCategory: async (id: number) => {
    const response = await api.delete<ApiResponse<void>>(`/api/categories/${id}`);
    return response.data;
  }
}; 
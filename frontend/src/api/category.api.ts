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
}; 
import api from './config';
import { ApiResponse } from '../types/api.types';

export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  status?: string;
}

export interface UserListResponse {
  totalItems: number;
  users: User[];
  currentPage: number;
  totalPages: number;
}

export interface UserResponse {
  user: User;
}

export interface UserQueryParams {
  page: number;
  limit: number;
  search?: string;
}

const userApi = {
  getUsers: async (params: UserQueryParams) =>
    await api.get<UserListResponse>('/api/user', { params }),

  getUserById: async (id: string) =>
    await api.get<ApiResponse<User>>(`/api/user/${id}`),

  createUser: async (userData: Omit<User, 'id'>) =>
    await api.post<ApiResponse<UserResponse>>('/api/user/register', userData),

  updateUser: async (id: string, userData: Partial<User>) =>
    await api.put<ApiResponse<User>>(`/api/user/${id}`, userData),

  deleteUser: async (id: string) =>
    await api.delete<ApiResponse<null>>(`/api/user/${id}`),
};

export default userApi;

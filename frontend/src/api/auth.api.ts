import api from './config';
import { ApiResponse, LoginRequest, LoginResponse } from '../types/api.types';

export const authApi = {
    login: (credentials: LoginRequest) =>
        api.post<ApiResponse<LoginResponse>>('/api/auth/login', credentials),
    
    logout: () => api.post('/api/auth/logout'),
    
    getCurrentUser: () =>
        api.get<ApiResponse<LoginResponse['user']>>('/api/auth/user'),
    
    refreshToken: (refreshToken: string) =>{
        const response = api.post<{ data: { token: string }, message: string }>('/api/auth/refresh-token', { refreshToken })
        return response;
    },
}; 
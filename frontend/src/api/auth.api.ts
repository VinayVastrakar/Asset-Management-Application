import api from './config';
import { ApiResponse, LoginRequest, LoginResponse } from '../types/api.types';

export const authApi = {
    login: (credentials: LoginRequest) =>
        api.post<ApiResponse<LoginResponse>>('/api/auth/login', credentials),
    
    logout: () => api.post('/api/auth/logout'),
    
    getCurrentUser: () =>
        api.get<ApiResponse<LoginResponse['user']>>('/api/auth/user'),
    
    refreshToken: () =>
        api.post<ApiResponse<{ token: string }>>('/api/auth/refresh-token'),
}; 
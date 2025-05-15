import api from './config';
import { ApiResponse, LoginRequest, LoginResponse } from '../types/api.types';

export const authApi = {
    login: (credentials: LoginRequest) =>
        api.post<ApiResponse<LoginResponse>>('/auth/login', credentials),
    
    logout: () => api.post('/auth/logout'),
    
    getCurrentUser: () =>
        api.get<ApiResponse<LoginResponse['user']>>('/auth/me'),
    
    refreshToken: () =>
        api.post<ApiResponse<{ token: string }>>('/auth/refresh-token'),
}; 
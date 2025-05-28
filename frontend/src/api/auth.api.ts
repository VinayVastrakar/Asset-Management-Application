// import api from './config';
import axios, { AxiosInstance } from 'axios';
import { ApiResponse, LoginRequest, LoginResponse } from '../types/api.types';
import { BASE_URL } from './config';

// Create axios instance with default config
const axiosAPI: AxiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    timeout: 20000, // 20 seconds
});


export const authApi = {
    login: (credentials: LoginRequest) =>
        axiosAPI.post<ApiResponse<LoginResponse>>('/api/auth/login', credentials),
    
    logout: () => axiosAPI.post('/api/auth/logout'),
    
    getCurrentUser: () =>
        axiosAPI.get<ApiResponse<LoginResponse['user']>>('/api/auth/user'),
    
    refreshToken: (refreshToken: string) => axiosAPI.post<{ data: { token: string }, message: string }>('/api/auth/refresh-token', { refreshToken }),

}; 
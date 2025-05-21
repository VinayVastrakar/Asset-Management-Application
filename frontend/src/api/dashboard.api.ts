import api from './config';
import { ApiResponse } from '../types/api.types';

export interface DashboardStats {
  totalAssets: number;
  totalUsers: number;
  categoryWise: Array<{
    category: string;
    count: number;
  }>;
}

export const dashboardApi = {
  getStats: async () =>
    await api.get<ApiResponse<DashboardStats>>('/api/dashboard/stats'),
}; 
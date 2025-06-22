import api from './config';
import { ApiResponse } from '../types/api.types';

export interface DepreciationRate {
  id: number;
  categoryId: number;
  categoryName: string;
  assetType?: string;
  financialYear: string;
  depreciationPercentage: number;
  depreciationMethod: string;
  usefulLifeYears?: number;
  residualValuePercentage?: number;
  effectiveFromDate: string;
  effectiveToDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DepreciationRateRequest {
  categoryId: number;
  assetType?: string;
  financialYear: string;
  depreciationPercentage: number;
  depreciationMethod: string;
  usefulLifeYears?: number;
  residualValuePercentage?: number;
  effectiveFromDate: string;
  effectiveToDate?: string;
}

export interface DepreciationRateQueryParams {
  page?: number;
  limit?: number;
  categoryId?: number;
  financialYear?: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
}

export const depreciationRateApi = {
  getDepreciationRates: async (params: DepreciationRateQueryParams) => {
    const response = await api.get<ApiResponse<PaginatedResponse<DepreciationRate>>>('/api/depreciation-rates', { params });
    return response.data;
  },

  getDepreciationRateById: async (id: number) => {
    const response = await api.get<ApiResponse<DepreciationRate>>(`/api/depreciation-rates/${id}`);
    return response.data;
  },

  createDepreciationRate: async (data: DepreciationRateRequest) => {
    const response = await api.post<ApiResponse<DepreciationRate>>('/api/depreciation-rates', data);
    return response.data;
  },

  updateDepreciationRate: async (id: number, data: DepreciationRateRequest) => {
    const response = await api.put<ApiResponse<DepreciationRate>>(`/api/depreciation-rates/${id}`, data);
    return response.data;
  },

  deleteDepreciationRate: async (id: number) => {
    const response = await api.delete<ApiResponse<void>>(`/api/depreciation-rates/${id}`);
    return response.data;
  },
}; 
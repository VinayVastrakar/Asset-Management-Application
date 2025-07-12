import api from './config';
import { ApiResponse } from '../types/api.types';
import { Api } from '@reduxjs/toolkit/query';

export interface PurchaseHistory {
  id: number;
  assetId: number;
  assetName: string;
  purchaseDate: string;
  amount: number;
  currentValue: number;
  vendor: string;
  notify: string;
  expiryDate: string;
  invoiceNumber: string;
  warrantyPeriod: number;
  description?: string;
  qty: number;
}

export interface PurchaseHistoryResponse {
  content: PurchaseHistory[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  totalCurrentValue: number;
}

export interface PurchaseHistoryQueryParams {
  page?: number;
  limit?: number;
  search?: string;
  assetId?: number;
  status?: string;
}

export const purchaseHistoryApi = {
  getPurchaseHistories: async (params: PurchaseHistoryQueryParams) => {
    const response = await api.get<PurchaseHistoryResponse>('/api/purchase-history', { params });
    return response.data;
  },

  // Download Excel report
  downloadExcel: async (params: { assetId?: number }) => {
    const query = params.assetId ? `?assetId=${params.assetId}` : '';
    const response = await api.get(`/api/purchase-history/export${query}`, {
      responseType: 'blob',
    });
    return response.data;
  },

  getPurchaseHistoryById: async (id: number) => {
    const response = await api.get<PurchaseHistory>(`/api/purchase-history/${id}`);
    return response.data;
  },

  createPurchaseHistory: async (data: Omit<PurchaseHistory, 'id'>) => {
    const response = await api.post<ApiResponse<PurchaseHistory>>('/api/purchase-history', data);
    return response.data;
  },

  updatePurchaseHistory: async (id: number, data: Partial<PurchaseHistory>) => {
    const response = await api.put<ApiResponse<PurchaseHistory>>(`/api/purchase-history/${id}`, data);
    return response.data;
  },

  deletePurchaseHistory: async (id: number) => {
    const response = await api.delete<ApiResponse<void>>(`/api/purchase-history/${id}`);
    return response.data;
  },

  createPurchaseHistoryWithBill: async (form: FormData) => {
    const response = await api.post('/api/purchase-history', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
  },

  updatePurchaseHistoryWithBill: async (id: number, form: FormData) => {
    const response = await api.put(`/api/purchase-history/${id}`, form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
  }
}; 
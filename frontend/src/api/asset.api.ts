import api from './config';
import { Asset } from '../redux/slices/assetSlice';

export interface AssetQueryParams {
  page?: number;
  limit?: number;
  search?: string;
  categoryId?: number;
  status?: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export const assetApi = {
  getAssets: async (params: AssetQueryParams) => {
    const response = await api.get<ApiResponse<PaginatedResponse<Asset>>>('/api/asset', { params });
    return response.data;
  },

  // getAssets: async (params: AssetQueryParams) => {
  //   const response = await api.get<ApiResponse<PaginatedResponse<Asset>>>('/api/asset', { params });
  //   console.log("Assets by page: ", response); // Better log
  //   return response.data; // This is the actual asset array
  // },

  getAssetById: async (id: number) => {
    const response = await api.get<Asset>(`/api/asset/${id}`);
    return response.data;
  },

  createAsset: async (formData: FormData) => {
    const response = await api.post<ApiResponse<Asset>>('/api/asset', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  updateAsset: async (id: number, formData: FormData) => {
    const response = await api.put<ApiResponse<Asset>>(`/api/asset/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  deleteAsset: async (id: number) => {
    const response = await api.delete<ApiResponse<null>>(`/api/asset/${id}`);
    return response.data;
  },
}; 
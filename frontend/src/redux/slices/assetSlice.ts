import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { assetApi, AssetQueryParams } from '../../api/asset.api';

export interface Asset {
  id: number;
  name: string;
  description: string;
  categoryId: number;
  purchaseDate: string;
  expiryDate: string;
  warrantyPeriod: number;
  assignedToUserName:string;
  status: string;
  imageUrl?: string;
}

export interface AssetState {
  assets: Asset[];
  currentAsset: Asset | null;
  loading: boolean;
  error: string | null;
  total: number;
  page: number;
  limit: number;
}

const initialState: AssetState = {
  assets: [],
  currentAsset: null,
  loading: false,
  error: null,
  total: 0,
  page: 0,
  limit: 10
};

export const fetchAssets = createAsyncThunk(
  'assets/fetchAssets',
  async (params: AssetQueryParams) => {
    const response = await assetApi.getAssets(params);
    return response.data;
  }
);

export const fetchAssetById = createAsyncThunk(
  'assets/fetchAssetById',
  async (id: number) => {
    const response = await assetApi.getAssetById(id);
    return response.data;
  }
);

export const addAsset = createAsyncThunk(
  'assets/add',
  async (formData: FormData) => {
    const response = await assetApi.createAsset(formData);
    return response.data;
  }
);

export const updateAsset = createAsyncThunk(
  'assets/updateAsset',
  async ({ id, formData }: { id: number; formData: FormData }) => {
    const response = await assetApi.updateAsset(id, formData);
    return response.data;
  }
);

export const deleteAsset = createAsyncThunk(
  'assets/deleteAsset',
  async (id: number) => {
    await assetApi.deleteAsset(id);
    return id;
  }
);

const assetSlice = createSlice({
  name: 'assets',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentAsset: (state) => {
      state.currentAsset = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Assets
      .addCase(fetchAssets.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAssets.fulfilled, (state, action) => {
        state.loading = false;
        state.assets = action.payload.data;
        state.total = action.payload.total;
        state.page = action.payload.page;
        state.limit = action.payload.limit;
      })
      .addCase(fetchAssets.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch assets';
      })
      // Fetch Asset by ID
      .addCase(fetchAssetById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAssetById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentAsset = action.payload;
      })
      .addCase(fetchAssetById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch asset';
      })
      // Add Asset
      .addCase(addAsset.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(addAsset.fulfilled, (state, action) => {
        state.loading = false;
        state.assets.push(action.payload);
        state.total += 1;
      })
      .addCase(addAsset.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to add asset';
      })
      // Update Asset
      .addCase(updateAsset.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateAsset.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.assets.findIndex(asset => asset.id === action.payload.id);
        if (index !== -1) {
          state.assets[index] = action.payload;
        }
        if (state.currentAsset?.id === action.payload.id) {
          state.currentAsset = action.payload;
        }
      })
      .addCase(updateAsset.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update asset';
      })
      // Delete Asset
      .addCase(deleteAsset.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAsset.fulfilled, (state, action) => {
        state.loading = false;
        state.assets = state.assets.filter(asset => asset.id !== action.payload);
        state.total -= 1;
        if (state.currentAsset?.id === action.payload) {
          state.currentAsset = null;
        }
      })
      .addCase(deleteAsset.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete asset';
      });
  },
});

export const { clearError, clearCurrentAsset } = assetSlice.actions;
export default assetSlice.reducer; 
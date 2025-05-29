import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { assetApi, AssetQueryParams } from '../../api/asset.api';

export interface Asset {
  id: number;
  name: string;
  description: string;
  categoryId: number;
  categoryName: string;
  purchaseDate: string;
  expiryDate: string;
  warrantyPeriod: number;
  assignedToUserName: string;
  status: string;
  imageUrl?: string;
}

export interface AssetState {
  assets: Record<number, Asset>;
  currentAsset: Asset | null;
  loading: boolean;
  updating: boolean;
  isLoaded: false;
  error: string | null;
  total: number;
  page: number;
  limit: number;
}

const initialState: AssetState = {
  assets: {},
  currentAsset: null,
  loading: false,
  updating: false,
  isLoaded: false,  
  error: null,
  total: 0,
  page: 0,
  limit: 10,
};

// Thunks
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
    return response;
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
  async ({ id, formData }: { id: number; formData: FormData }, thunkAPI) => {
    try {
      const response = await assetApi.updateAsset(id, formData);
      return response.data;
    } catch (error: any) {
      console.error('Error in updateAsset thunk:', error);
      return thunkAPI.rejectWithValue(error?.response?.data?.message || 'Update failed');
    }
  }
);

export const deleteAsset = createAsyncThunk(
  'assets/deleteAsset',
  async (id: number) => {
    await assetApi.deleteAsset(id);
    return id;
  }
);

export const inactiveAsset = createAsyncThunk(
  'assets/inactiveAsset',
  async (id: number) => {
    await assetApi.inactiveAsset(id);
    return id;
  }
);

export const activeAsset = createAsyncThunk(
  'assets/activeAsset',
  async (id: number) => {
    await assetApi.activeAsset(id);
    return id;
  }
);

// Slice
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
        const assetArray = action.payload.data;
        state.assets = assetArray.reduce((acc, asset) => {
          acc[asset.id] = asset;
          return acc;
        }, {} as Record<number, Asset>);
        state.total = action.payload.total;
        state.page = action.payload.page;
        state.limit = action.payload.limit;
        state.isLoaded = true;
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
        state.assets[action.payload.id] = action.payload;
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
        state.assets[action.payload.id] = action.payload;
        state.total += 1;
      })
      .addCase(addAsset.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to add asset';
      })

      // Update Asset
      .addCase(updateAsset.pending, (state) => {
        state.updating = true;
        state.error = null;
      })
      .addCase(updateAsset.fulfilled, (state, action) => {
        state.updating = false;
        const asset = action.payload;
        if (asset?.id) {
          state.assets[asset.id] = asset;
          if (state.currentAsset?.id === asset.id) {
            state.currentAsset = asset;
          }
        }
      })
      .addCase(updateAsset.rejected, (state, action) => {
        state.updating = false;
        state.error = action.error.message || 'Failed to update asset';
      })

      // Delete Asset
      .addCase(deleteAsset.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAsset.fulfilled, (state, action) => {
        state.loading = false;
        delete state.assets[action.payload];
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

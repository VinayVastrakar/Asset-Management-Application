import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { assetApi } from '../../api/asset.api';

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
  stolenNotes?: string;
  disposedNotes?: string;
}

interface AssetState {
  currentAsset: Asset | null;
  loading: boolean;
  updating: boolean;
  error: string | null;
}

const initialState: AssetState = {
  currentAsset: null,
  loading: false,
  updating: false,
  error: null,
};

// Keep only global asset-level actions
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

// Slice
const assetSlice = createSlice({
  name: 'assets',
  initialState,
  reducers: {
    clearCurrentAsset: (state) => {
      state.currentAsset = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
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

      .addCase(addAsset.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(addAsset.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(addAsset.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to add asset';
      })

      .addCase(updateAsset.pending, (state) => {
        state.updating = true;
        state.error = null;
      })
      .addCase(updateAsset.fulfilled, (state, action) => {
        state.updating = false;
        if (state.currentAsset?.id === action.payload.id) {
          state.currentAsset = action.payload;
        }
      })
      .addCase(updateAsset.rejected, (state, action) => {
        state.updating = false;
        state.error = action.payload as string || 'Failed to update asset';
      })

      .addCase(deleteAsset.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAsset.fulfilled, (state, action) => {
        state.loading = false;
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

export const { clearCurrentAsset, clearError } = assetSlice.actions;
export default assetSlice.reducer;

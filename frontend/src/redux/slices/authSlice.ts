import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authApi } from 'api/auth.api';
import api from 'api/config';
import axios from 'axios';

// --- Constants for storage keys ---
const STORAGE_KEYS = {
  token: 'token',
  refreshToken: 'refreshToken',
  rememberMe: 'rememberMe',
  user: 'user',
};

// --- Types ---
interface User {
  id: number;
  email: string;
  name: string;
  role: string;
}

interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
  rememberMe: boolean;
}

// --- Helpers for storage ---
const getStoredUser = (): User | null => {
  const userStr = localStorage.getItem(STORAGE_KEYS.user) || sessionStorage.getItem(STORAGE_KEYS.user);
  return userStr ? JSON.parse(userStr) : null;
};

const getToken = (): string | null =>
  localStorage.getItem(STORAGE_KEYS.token) || sessionStorage.getItem(STORAGE_KEYS.token);

const getRefreshToken = (): string | null =>
  localStorage.getItem(STORAGE_KEYS.refreshToken) || sessionStorage.getItem(STORAGE_KEYS.refreshToken);

// --- Initial State ---
const initialState: AuthState = {
  user: getStoredUser(),
  token: getToken(),
  refreshToken: getRefreshToken(),
  isAuthenticated: !!getToken(),
  loading: false,
  error: null,
  rememberMe: localStorage.getItem(STORAGE_KEYS.rememberMe) === 'true',
};

// --- Restore Axios Authorization header ---
if (initialState.token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${initialState.token}`;
}

// --- Axios Interceptor for Token Refresh ---
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    alert("Error Message: ")
    const originalRequest = error.config;
    if ((error.response?.status === 401 || error.response?.status === 403) && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshToken = localStorage.getItem(STORAGE_KEYS.refreshToken) || sessionStorage.getItem(STORAGE_KEYS.refreshToken);
        if (!refreshToken) {
          window.location.href = '/login';
          return Promise.reject('Refresh token missing');
        }
        const response = await authApi.refreshToken(refreshToken);
        console.log(response);
        const { token } = response.refreshToken;

        // Store new token
        if (localStorage.getItem(STORAGE_KEYS.rememberMe) === 'true') {
          localStorage.setItem(STORAGE_KEYS.token, token);
        } else {
          sessionStorage.setItem(STORAGE_KEYS.token, token);
        }

        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        originalRequest.headers['Authorization'] = `Bearer ${token}`;
        return axios(originalRequest);
      } catch (refreshError) {
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

// --- Async Thunks ---
export const login = createAsyncThunk(
  'auth/login',
  async (
    credentials: { email: string; password: string; rememberMe?: boolean },
    { rejectWithValue }
  ) => {
    try {
      const response = await api.post('/api/auth/login', credentials);
      const { token, refreshToken, user } = response.data;

      if (credentials.rememberMe) {
        localStorage.setItem(STORAGE_KEYS.token, token);
        localStorage.setItem(STORAGE_KEYS.refreshToken, refreshToken);
        localStorage.setItem(STORAGE_KEYS.rememberMe, 'true');
        localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
      } else {
        sessionStorage.setItem(STORAGE_KEYS.token, token);
        sessionStorage.setItem(STORAGE_KEYS.refreshToken, refreshToken);
        localStorage.setItem(STORAGE_KEYS.rememberMe, 'false');
        sessionStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
      }

      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      return { token, refreshToken, user };
    } catch (error: any) {
      const message = error.response?.data?.error || 'Login failed. Please check your credentials.';
      return rejectWithValue(message);
    }
  }
);

export const refreshToken = createAsyncThunk(
  'auth/refreshToken',
  async (_, { rejectWithValue }) => {
    try {
      const refreshToken =
        localStorage.getItem(STORAGE_KEYS.refreshToken) || sessionStorage.getItem(STORAGE_KEYS.refreshToken);
      if (!refreshToken) {
        return rejectWithValue('Refresh token missing');
      }
      const response = await authApi.refreshToken(refreshToken);
      const { token } = response.refreshToken;

      if (localStorage.getItem(STORAGE_KEYS.rememberMe) === 'true') {
        localStorage.setItem(STORAGE_KEYS.token, token);
      } else {
        sessionStorage.setItem(STORAGE_KEYS.token, token);
      }

      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      return { token };
    } catch (error: any) {
      return rejectWithValue('Session expired. Please login again.');
    }
  }
);

// --- Slice ---
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.rememberMe = false;

      // Remove only auth-related keys
      localStorage.removeItem(STORAGE_KEYS.token);
      localStorage.removeItem(STORAGE_KEYS.refreshToken);
      localStorage.removeItem(STORAGE_KEYS.rememberMe);
      localStorage.removeItem(STORAGE_KEYS.user);

      sessionStorage.removeItem(STORAGE_KEYS.token);
      sessionStorage.removeItem(STORAGE_KEYS.refreshToken);
      sessionStorage.removeItem(STORAGE_KEYS.user);

      delete axios.defaults.headers.common['Authorization'];
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.isAuthenticated = true;
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.refreshToken = action.payload.refreshToken;
        state.error = null;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(refreshToken.pending, (state) => {
        state.loading = true;
      })
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.loading = false;
        state.token = action.payload.token;
        state.error = null;
      })
      .addCase(refreshToken.rejected, (state, action) => {
        state.loading = false;
        state.isAuthenticated = false;
        state.user = null;
        state.token = null;
        state.refreshToken = null;
        state.error = action.payload as string;
      });
  },
});

export const { logout, clearError } = authSlice.actions;
export default authSlice.reducer;

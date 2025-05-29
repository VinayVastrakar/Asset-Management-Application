import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import userApi, { User, UserQueryParams } from '../../api/user.api';

export interface UserState {
  users: User[];
  currentUser: User | null;
  loading: boolean;
  fetchLoading: boolean;
  updateLoading: boolean;
  error: string | null;
  total: number;
  page: number;
  limit: number;
}

const initialState: UserState = {
  users: [],
  currentUser: null,
  loading: false,
  fetchLoading: false,
  updateLoading: false,
  error: null,
  total: 0,
  page: 0,
  limit: 10
};

export const fetchUsers = createAsyncThunk(
  'users/fetchUsers',
  async (params: UserQueryParams) => {
    const response = await userApi.getUsers(params);
    if (!response.data?.users) {
      throw new Error('Malformed API response');
    }
    return response.data;
  },
  {
    condition: (_, { getState }) => {
      const state = getState() as { users: UserState };
      return state.users.users.length === 0; // ⛔️ Cancel if users already loaded
    }
  }
);

export const fetchUserById = createAsyncThunk(
  'users/fetchUserById',
  async (id: string) => {
    const response = await userApi.getUserById(id);
    return response.data;
  }
);

export const addUser = createAsyncThunk(
  'users/addUser',
  async (userData: Omit<User, 'id'>) => {
    const response = await userApi.createUser(userData);
    console.log(response);
    return response.data.data.user;
  }
);

export const updateUser = createAsyncThunk(
  'users/updateUser',
  async ({ id, userData }: { id: string; userData: Partial<User> }) => {
    const response = await userApi.updateUser(id, userData);
    console.log(response.data);
    return response.data;
  }
);

export const deleteUser = createAsyncThunk(
  'users/deleteUser',
  async (id: string) => {
    await userApi.deleteUser(id);
    return id;
  }
);

export const inactiveUser = createAsyncThunk(
  'users/Inactive',
  async (id: string) => {
    await userApi.inactive(id);
    return id;
  }
);

export const activeUser = createAsyncThunk(
  'users/Acctive',
  async (id: string) => {
    await userApi.active(id);
    return id;
  }
);

const userSlice = createSlice({
  name: 'users',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentUser: (state) => {
      state.currentUser = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Users
      .addCase(fetchUsers.pending, (state) => {
        state.fetchLoading = true;
        state.error = null;
      })
      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.fetchLoading = false;
        state.users = action.payload.users;
        state.total = action.payload.totalItems;
        state.page = action.payload.currentPage;
      })
      .addCase(fetchUsers.rejected, (state, action) => {
        state.fetchLoading = false;
        state.error = action.error.message || 'Failed to fetch users';
      })
      // Fetch User by ID
      .addCase(fetchUserById.pending, (state) => {
        state.fetchLoading = true;
        state.error = null;
      })
      .addCase(fetchUserById.fulfilled, (state, action) => {
        state.fetchLoading = false;
        state.currentUser = action.payload;
      })
      .addCase(fetchUserById.rejected, (state, action) => {
        state.fetchLoading = false;
        state.error = action.error.message || 'Failed to fetch user';
      })
      // Add User
      .addCase(addUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(addUser.fulfilled, (state, action) => {
        state.loading = false;
        state.users.push(action.payload);
        state.total += 1;
      })
      .addCase(addUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to add user';
      })
      // Update User
      .addCase(updateUser.pending, (state) => {
        state.updateLoading = true;
        state.error = null;
      })
      .addCase(updateUser.fulfilled, (state, action) => {
        state.updateLoading = false;
        const index = state.users.findIndex(user => user.id === action.payload.id);
        if (index !== -1) {
          state.users[index] = action.payload;
        }
        if (state.currentUser?.id === action.payload.id) {
          state.currentUser = action.payload;
        }
      })
      .addCase(updateUser.rejected, (state, action) => {
        state.updateLoading = false;
        state.error = action.error.message || 'Failed to update user';
      })
      // Delete User
      .addCase(deleteUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteUser.fulfilled, (state, action) => {
        state.loading = false;
        state.users = state.users.filter(user => user.id !== Number(action.payload));
        state.total -= 1;
      })
      .addCase(deleteUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete user';
      });
  },
});

export const { clearError, clearCurrentUser } = userSlice.actions;
export default userSlice.reducer; 
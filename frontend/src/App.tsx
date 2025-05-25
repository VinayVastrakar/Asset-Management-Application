import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './redux/store';
import Login from './components/auth/Login';
import Dashboard from './components/dashboard/Dashboard';
import PrivateRoute from './components/auth/PrivateRoute';
import ForgotPassword from './components/auth/ForgotPassword';
import ValidateOTP from './components/auth/ValidateOTP';
import ResetPassword from './components/auth/ResetPassword';
import Layout from './components/layout/Layout';
import ProtectedRoute from 'routes/ProtectedRoute';
import AddUser from 'components/user/AddUser';
import UserList from 'components/user/UserList';
import EditUser from 'components/user/EditUser';
import AddAsset from 'components/asset/AddAsset';

const App: React.FC = () => {
  return (
    <Provider store={store}>
      <Router>
      <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/validate-otp" element={<ValidateOTP />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/" element={<Login />} />

          <Route
            path="/dashboard"
            element={
              <PrivateRoute allowedRoles={['ADMIN', 'USER']}>
                <Layout>
                  <Dashboard />
                </Layout>
              </PrivateRoute>
            }
          />

          <Route
            path="/users"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <UserList />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/users/add"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <AddUser />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/users/edit/:id"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <EditUser />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/assets/add"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <AddAsset />
                </Layout>
              </ProtectedRoute>
            }
          />
        </Routes>
      </Router>
    </Provider>
  );
};

export default App; 
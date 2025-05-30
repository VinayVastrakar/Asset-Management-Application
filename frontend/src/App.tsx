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
import AssetList from 'components/asset/AssetList';
import AssetView from 'components/asset/AssetView';
import EditAsset from 'components/asset/EditAsset';
import AddCategory from 'components/category/AddCategory';
import ListCategory from 'components/category/ListCategory';
import EditCategory from 'components/category/EditCategory';

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
          <Route
            path="/assets"
            element={
              <ProtectedRoute>
                <Layout>
                  <AssetList />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/assets/:id"
            element={
              <ProtectedRoute>
                <Layout>
                  <AssetView />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/assets/edit/:id"
            element = {
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>  
                  <EditAsset/>
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/categories/add"
            element = {
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>  
                  <AddCategory/>
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/categories/manage"
            element = {
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>  
                  <ListCategory/>
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/categories/edit/:id"
            element = {
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>  
                  <EditCategory/>
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
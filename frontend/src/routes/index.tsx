import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from '../components/auth/Login';
import Dashboard from '../components/dashboard/Dashboard';
import UserRoutes from './userRoutes';
import AssetRoutes from './assetRoutes';
import ProtectedRoute from './ProtectedRoute';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route path="/users/*" element={<UserRoutes />} />
      <Route path="/assets/*" element={<AssetRoutes />} />
    </Routes>
  );
};

export default AppRoutes; 
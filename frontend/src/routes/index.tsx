import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from '../components/auth/Login';
import Dashboard from '../components/dashboard/Dashboard';
import UserRoutes from './UserRoutes';
import AssetRoutes from './AssetRoutes';
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
      <Route path="/users/*" element={
        <ProtectedRoute>
          <UserRoutes />
        </ProtectedRoute>
      } />
      <Route path="/assets/*" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <AssetRoutes />
        </ProtectedRoute>
        } />
    </Routes>
  );
};

export default AppRoutes; 
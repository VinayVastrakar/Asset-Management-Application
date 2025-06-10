import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Dashboard from '../components/dashboard/Dashboard';
import UserRoutes from './UserRoutes';
import AssetRoutes from './AssetRoutes';
import CategoryRoutes from './CategoryRoutes';
import PurchaseHistoryRoutes from './PurchaseHistoryRoutes';
import ProtectedRoute from './ProtectedRoute';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={
        <ProtectedRoute allowedRoles={['ADMIN', 'USER']}>
          <Dashboard />
        </ProtectedRoute>
      } />
      <Route path="/dashboard" element={
        <ProtectedRoute allowedRoles={['ADMIN', 'USER']}>
          <Dashboard />
        </ProtectedRoute>
      } />
      <Route path="/users/*" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
            <UserRoutes />
        </ProtectedRoute>} />
      <Route path="/assets/*" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
            <AssetRoutes />
        </ProtectedRoute>} />
      <Route path="/categories/*" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
            <CategoryRoutes />
        </ProtectedRoute>} />
      <Route path="/purchase-history/*" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
            <PurchaseHistoryRoutes />
        </ProtectedRoute>} />
    </Routes>
  );
};

export default AppRoutes;
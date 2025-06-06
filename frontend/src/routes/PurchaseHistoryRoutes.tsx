import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ListPurchaseHistory from '../components/purchaseHistory/ListPurchaseHistory';
import AddPurchaseHistory from '../components/purchaseHistory/AddPurchaseHistory';
import ProtectedRoute from './ProtectedRoute';

const PurchaseHistoryRoutes = () => {
  return (
    <Routes>
      <Route index element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <ListPurchaseHistory />
        </ProtectedRoute>
      } />
      <Route path="add" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <AddPurchaseHistory />
        </ProtectedRoute>
      } />
    </Routes>
  );
};

export default PurchaseHistoryRoutes; 
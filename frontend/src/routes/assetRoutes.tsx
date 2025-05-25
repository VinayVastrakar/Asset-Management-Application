import React from 'react';
import { Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import AddAsset from '../components/asset/AddAsset';

const AssetRoutes = () => {
  return (
    <>
      <Route
        path="/assets/add"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AddAsset />
          </ProtectedRoute>
        }
      />
      {/* Add more asset routes here */}
    </>
  );
};

export default AssetRoutes; 
import React from 'react';
import { Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import AddAsset from '../components/asset/AddAsset';
import AssetList from '../components/asset/AssetList';
import AssetView from '../components/asset/AssetView';

const AssetRoutes = () => {
  return (
    <>
      <Route
        path="/assets"
        element={
          <ProtectedRoute>
            <AssetList />
          </ProtectedRoute>
        }
      />
      <Route
        path="/assets/:id"
        element={
          <ProtectedRoute>
            <AssetView />
          </ProtectedRoute>
        }
      />
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
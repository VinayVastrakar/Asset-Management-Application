import { Routes, Route } from 'react-router-dom';
import AssetList from '../components/asset/AssetList';
import AssetView from '../components/asset/AssetView';
import AddAsset from '../components/asset/AddAsset';
import EditAsset from '../components/asset/EditAsset';
import ProtectedRoute from './ProtectedRoute';

const AssetRoutes = () => {
  return (
    <Routes>
      <Route index element={
        <ProtectedRoute>
          <AssetList />
        </ProtectedRoute>
      } />
      <Route path=":id" element={
        <ProtectedRoute>
          <AssetView />
        </ProtectedRoute>
      } />
      <Route path="add" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <AddAsset />
        </ProtectedRoute>
      } />
      <Route path="edit/:id" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <EditAsset />
        </ProtectedRoute>
      } />
    </Routes>
  );
};

export default AssetRoutes;
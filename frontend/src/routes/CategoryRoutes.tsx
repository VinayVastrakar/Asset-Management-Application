import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ListCategory from '../components/category/ListCategory';
import AddCategory from '../components/category/AddCategory';
import EditCategory from '../components/category/EditCategory';
import ProtectedRoute from './ProtectedRoute';

const CategoryRoutes = () => {
  return (
    <Routes>
      <Route index element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <ListCategory />
        </ProtectedRoute>
      } />
      <Route path="add" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <AddCategory />
        </ProtectedRoute>
      } />
      <Route path="edit/:id" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <EditCategory />
        </ProtectedRoute>
      } />
      <Route path="manage" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <ListCategory />
        </ProtectedRoute>
      } />
    </Routes>
  );
};

export default CategoryRoutes;
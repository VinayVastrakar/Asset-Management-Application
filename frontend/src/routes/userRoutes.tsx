import React from 'react';
import { Route } from 'react-router-dom';
import UserList from '../components/user/UserList';
import AddUser from '../components/user/AddUser';
import EditUser from '../components/user/EditUser';
import ProtectedRoute from './ProtectedRoute';

const UserRoutes = () => {
  return (
    <>
      <Route
        path="/users"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <UserList />
          </ProtectedRoute>
        }
      />
      <Route
        path="/users/add"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AddUser />
          </ProtectedRoute>
        }
      />
      <Route
        path="/users/edit/:id"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <EditUser />
          </ProtectedRoute>
        }
      />
    </>
  );
};

export default UserRoutes; 
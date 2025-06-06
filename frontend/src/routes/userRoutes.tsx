import { Routes, Route } from 'react-router-dom';
import UserList from '../components/user/UserList';
import AddUser from '../components/user/AddUser';
import EditUser from '../components/user/EditUser';
import ProtectedRoute from './ProtectedRoute';

const UserRoutes = () => {
  return (
    <Routes>
      <Route index element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <UserList />
        </ProtectedRoute>
      } />
      <Route path="add" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <AddUser />
        </ProtectedRoute>
      } />
      <Route path="edit/:id" element={
        <ProtectedRoute allowedRoles={['ADMIN']}>
          <EditUser />
        </ProtectedRoute>
      } />
    </Routes>
  );
};

export default UserRoutes;
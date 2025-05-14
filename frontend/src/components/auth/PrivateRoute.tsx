import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/store';

interface PrivateRouteProps {
  children: React.ReactNode;
  allowedRoles:string[];
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children,allowedRoles }) => {
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

export default PrivateRoute; 
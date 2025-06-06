import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from '../components/auth/Login';
import ForgotPassword from '../components/auth/ForgotPassword';
import ValidateOTP from '../components/auth/ValidateOTP';
import ResetPassword from '../components/auth/ResetPassword';

const AuthRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/validate-otp" element={<ValidateOTP />} />
      <Route path="/reset-password" element={<ResetPassword />} />
    </Routes>
  );
};

export default AuthRoutes;
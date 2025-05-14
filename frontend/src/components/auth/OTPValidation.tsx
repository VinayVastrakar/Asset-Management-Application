import React, { useState } from "react";
import { useLocation } from "react-router-dom";

const OTPValidation: React.FC = () => {
  const { state } = useLocation();
  const [otp, setOTP] = useState("");
  const [newPassword, setNewPassword] = useState("");

  const handleValidate = async (e: React.FormEvent) => {
    e.preventDefault();
    // Call API to validate OTP and set new password
  };

  return (
    <form onSubmit={handleValidate} className="...">
      <input type="email" value={state.email} readOnly />
      <input type="text" value={otp} onChange={e => setOTP(e.target.value)} required />
      <input type="password" value={newPassword} onChange={e => setNewPassword(e.target.value)} required />
      <button type="submit">Reset Password</button>
    </form>
  );
};

export default OTPValidation; 
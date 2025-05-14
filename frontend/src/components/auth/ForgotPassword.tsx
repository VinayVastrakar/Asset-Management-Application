import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const handleSendOTP = async (e: React.FormEvent) => {
    e.preventDefault();
    // Call API to send OTP
    // On success:
    navigate("/otp-validation", { state: { email } });
  };

  return (
    <form onSubmit={handleSendOTP} className="...">
      <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />
      <button type="submit">Send OTP</button>
    </form>
  );
};

export default ForgotPassword; 
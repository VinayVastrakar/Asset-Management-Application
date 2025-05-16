import api from './config';
import {
    ApiResponse,
    ForgotPasswordRequest,
    ForgotPasswordResponse,
    ValidateOTPRequest,
    ValidateOTPResponse,
    ResetPasswordRequest,
    ResetPasswordResponse
} from '../types/api.types';

export const passwordApi = {
    // Request password reset
    forgotPassword: (data: ForgotPasswordRequest) =>
        api.post<ApiResponse<ForgotPasswordResponse>>('/api/auth/forgot-password', data),

    // Validate OTP
    validateOTP: (data: ValidateOTPRequest) =>
        api.post<ApiResponse<ValidateOTPResponse>>('/api/auth/validate-otp', data),

    // Reset password with OTP
    resetPassword: (data: ResetPasswordRequest) =>
        api.post<ApiResponse<ResetPasswordResponse>>('/api/auth/reset-password', data),

    // Resend OTP
    resendOTP: (email: string) =>
        api.post<ApiResponse<{ message: string }>>('/api/auth/resend-otp', { email }),
}; 
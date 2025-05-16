export interface ApiResponse<T> {
    data: T;
    message: string;
    status: number;
    error?: string;
}

export interface ErrorResponse {
    message: string;
    status: number;
    errors?: Record<string, string[]>;
    error?: string;
}

export interface LoginResponse {
    token: string;
    user: {
        id: number;
        email: string;
        name: string;
        role: string;
    };
}

export interface LoginRequest {
    email: string;
    password: string;
}

// Forgot Password Types
export interface ForgotPasswordRequest {
    email: string;
}

export interface ForgotPasswordResponse {
    message: string;
    email: string;
    error?: string;
}

// OTP Validation Types
export interface ValidateOTPRequest {
    email: string;
    otp: string;
}

export interface ValidateOTPResponse {
    isValid: boolean;
    message: string;
}

// Reset Password Types
export interface ResetPasswordRequest {
    email: string;
    otp: string;
    newPassword: string;
    confirmPassword: string;
}

export interface ResetPasswordResponse {
    message: string;
    success: boolean;
} 
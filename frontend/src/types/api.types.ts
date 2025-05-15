export interface ApiResponse<T> {
    data: T;
    message: string;
    status: number;
}

export interface ErrorResponse {
    message: string;
    status: number;
    errors?: Record<string, string[]>;
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
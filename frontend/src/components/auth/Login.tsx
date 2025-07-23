import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { login, clearError } from '../../redux/slices/authSlice';
import { AppDispatch, RootState } from '../../redux/store';
import { Alert } from '../common/Alert';

const Login: React.FC = () => {
  const [credentials, setCredentials] = useState({
    email: '',
    password: '',
    rememberMe: false
  });
  const [loginAttempts, setLoginAttempts] = useState(0);
  const [isLocked, setIsLocked] = useState(false);
  const [lockTimer, setLockTimer] = useState<number>(0);
  const [showPassword, setShowPassword] = useState(false);

  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { loading, error } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    // Clear any existing errors when component mounts
    dispatch(clearError());

    // Check if account is locked
    const lockedUntil = localStorage.getItem('loginLockedUntil');
    if (lockedUntil) {
      const timeLeft = parseInt(lockedUntil) - Date.now();
      if (timeLeft > 0) {
        setIsLocked(true);
        setLockTimer(Math.ceil(timeLeft / 1000));
      } else {
        localStorage.removeItem('loginLockedUntil');
        setLoginAttempts(0);
      }
    }
  }, [dispatch]);
  // Lock Timer Countdown
  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isLocked && lockTimer > 0) {
      interval = setInterval(() => {
        setLockTimer((prev) => {
          if (prev <= 1) {
            setIsLocked(false);
            localStorage.removeItem('loginLockedUntil');
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isLocked, lockTimer]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (isLocked) {
      return;
    }

    try {
      await dispatch(login(credentials)).unwrap();
      setLoginAttempts(0);
      navigate('/dashboard');
    } catch (error) {
      const newAttempts = loginAttempts + 1;
      setLoginAttempts(newAttempts);
      
      if (newAttempts >= 5) {
        const lockDuration = 1 * 60 * 1000; // 1 minutes
        const lockedUntil = Date.now() + lockDuration;
        localStorage.setItem('loginLockedUntil', lockedUntil.toString());
        setIsLocked(true);
        setLockTimer(lockDuration / 1000);
      }
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setCredentials({
      ...credentials,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <img
            className="mx-auto h-28 w-auto"
            src="/Assets/Gloitel.jpg"
            alt="Company Logo"
          />
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
        </div>
        
        {error && (
          <Alert type="error" message={error} onClose={() => dispatch(clearError())} />
        )}

        {isLocked && (
          <Alert
            type="warning"
            message={`Too many login attempts. Please try again in ${lockTimer} seconds.`}
          />
        )}

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <label htmlFor="email" className="sr-only">Email address</label>
              <input
                id="email"
                name="email"
                type="email"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-primary focus:border-primary focus:z-10 sm:text-sm"
                placeholder="Email address"
                value={credentials.email}
                onChange={handleChange}
                disabled={isLocked}
              />
            </div>
            <div>
              <label htmlFor="password" className="sr-only">Password</label>
              <div className="relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-primary focus:border-primary focus:z-10 sm:text-sm pr-10"
                  placeholder="Password"
                  value={credentials.password}
                  onChange={handleChange}
                  disabled={isLocked}
                />
                <button
                  type="button"
                  tabIndex={-1}
                  onClick={() => setShowPassword((prev) => !prev)}
                  className="absolute inset-y-0 right-0 flex items-center px-3 focus:outline-none group"
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  <span className="rounded-full p-1 transition-all duration-200 group-hover:bg-gray-200 group-hover:scale-110">
                    {showPassword ? (
                      // Modern Eye-off SVG (Heroicons 2.0 style)
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        strokeWidth={1.8}
                        stroke="currentColor"
                        className="h-5 w-5 text-gray-600 group-hover:text-primary transition-colors duration-200"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          d="M3.98 8.223A10.477 10.477 0 002.062 12C3.53 16.09 7.388 19 12 19c1.658 0 3.233-.335 4.646-.94m3.374-2.14A10.45 10.45 0 0021.938 12c-1.468-4.09-5.326-7-9.938-7-1.658 0-3.233.335-4.646.94M15 12a3 3 0 11-6 0 3 3 0 016 0zm-9 9l18-18"
                        />
                      </svg>
                    ) : (
                      // Modern Eye SVG (Heroicons 2.0 style)
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        strokeWidth={1.8}
                        stroke="currentColor"
                        className="h-5 w-5 text-gray-600 group-hover:text-primary transition-colors duration-200"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          d="M2.25 12C3.5 7.5 7.5 4.5 12 4.5s8.5 3 9.75 7.5c-1.25 4.5-5.25 7.5-9.75 7.5s-8.5-3-9.75-7.5z"
                        />
                        <circle cx="12" cy="12" r="3" />
                      </svg>
                    )}
                  </span>
                </button>
              </div>
            </div>
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <input
                id="remember-me"
                name="rememberMe"
                type="checkbox"
                className="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded"
                checked={credentials.rememberMe}
                onChange={handleChange}
                disabled={isLocked}
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
                Remember me
              </label>
            </div>

            <div className="text-sm">
              <Link
                to="/forgot-password"
                className="font-medium text-primary hover:text-primary-dark"
              >
                Forgot your password?
              </Link>
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading || isLocked}
              className={`group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white ${
                loading || isLocked
                  ? 'bg-gray-400 cursor-not-allowed'
                  : 'bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary'
              }`}
            >
              {loading ? (
                <span className="flex items-center">
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Signing in...
                </span>
              ) : (
                'Sign in'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login; 
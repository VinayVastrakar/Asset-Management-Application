import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../redux/store';
import { logout } from '../../redux/slices/authSlice';

const Header: React.FC = () => {
  const dispatch = useDispatch();
  const { user } = useSelector((state: RootState) => state.auth);

  return (
    <header className="bg-white shadow-md w-full">
      <div className="flex justify-between items-center h-16 px-4 sm:px-6 lg:px-8 w-full">
        <div className="flex items-center">
          <img
            className="h-16 w-auto"
            src="/Assets/Gloitel.jpg"
            alt="Company Logo"
          />
        </div>
        {user && (
          <div className="flex items-center space-x-4">
            <span className="text-gray-700">
              Welcome, {user.name} ({user.role})
            </span>
            <button
              onClick={() => dispatch(logout())}
              className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md text-sm font-medium"
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header; 
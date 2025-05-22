import React from 'react';
import Header from '../common/Header';
import Footer from '../common/Footer';
import Navbar from '../common/Navbar';
import { Alert } from '../common/Alert';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/store';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { error } = useSelector((state: RootState) => state.auth);

  return (
    <div className="min-h-screen flex flex-col">
      {/* Sticky Header */}
      <div className="sticky top-0 z-50 bg-white shadow">
        <Header />
      </div>

      {/* Sticky Navbar below Header */}
      <div className="sticky top-[64px] z-40 bg-gray-100 shadow">
        {/* Adjust `top-[64px]` if your header height is different */}
        <Navbar />
      </div>

      {/* Main Content */}
      <main className="flex-grow pt-4">
        {error && (
          <div className="container mx-auto px-4 py-4">
            <Alert type="error" message={error} />
          </div>
        )}
        {children}
      </main>

      <Footer />
    </div>
  );
};

export default Layout;

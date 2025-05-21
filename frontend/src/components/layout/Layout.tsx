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
      <Header />
      <Navbar />
      <main className="flex-grow">
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
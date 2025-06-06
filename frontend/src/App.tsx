import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './redux/store';
import Layout from './components/layout/Layout';
import AuthRoutes from './routes/AuthRoutes';
import AppRoutes from './routes/AppRoutes';

const App: React.FC = () => {
  return (
    <Provider store={store}>
      <Router>
        <AuthRoutes />
        <Layout>
          <AppRoutes />
        </Layout>
      </Router>
    </Provider>
  );
};

export default App;
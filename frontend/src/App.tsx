import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { Header } from './components/Header';
import { HomePage } from './pages/HomePage';
import { DashboardPage } from './pages/DashboardPage';

/**
 * Root application layout and React Router configuration.
 * Routes:
 *   /                   -> HomePage
 *   /train/:trainNumber -> DashboardPage
 */
export const App: React.FC = () => {
  return (
    <div className="app-shell">
      <Header />
      <main className="app-main">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/train/:trainNumber" element={<DashboardPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
};

export default App;

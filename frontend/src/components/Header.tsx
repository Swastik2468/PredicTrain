import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

type ThemeMode = 'light' | 'dark';

function getInitialTheme(): ThemeMode {
  const saved = localStorage.getItem('predictrack-theme');
  if (saved === 'light' || saved === 'dark') {
    return saved;
  }
  return 'light';
}

/**
 * Clean, minimal header with brand link and Light/Dark theme switch.
 */
export const Header: React.FC = () => {
  const [theme, setTheme] = useState<ThemeMode>(getInitialTheme);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('predictrack-theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'dark' ? 'light' : 'dark'));
  };

  return (
    <header className="app-header">
      <div className="header-inner">
        <Link to="/" className="brand-link">
          <span className="brand-title">PredicTrack</span>
        </Link>
        <nav className="header-nav">
          <Link to="/" className="header-nav-link">
            Search
          </Link>
          <button
            type="button"
            className="theme-toggle-btn"
            onClick={toggleTheme}
            aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
          >
            {theme === 'dark' ? 'Light' : 'Dark'}
          </button>
        </nav>
      </div>
    </header>
  );
};

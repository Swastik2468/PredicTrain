import React from 'react';
import { TrainSearch } from '../components/TrainSearch';

/**
 * Clean, focused landing page for PredicTrack (Section 7).
 */
export const HomePage: React.FC = () => {
  return (
    <section className="home-container">
      <div className="card search-card">
        <div className="search-card-header">
          <h1 className="page-title">PredicTrack</h1>
          <p className="page-subtitle">
            Dynamic Railway ETA &amp; Delay Prediction
          </p>
        </div>

        <TrainSearch />
      </div>
    </section>
  );
};

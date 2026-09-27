import React from 'react';

/**
 * Skeleton loader that mirrors the structure of the final dashboard layout
 * (TrainHeader, CurrentPosition, ETA + Delay cards, Breakdown, and Route Timeline).
 */
export const LoadingSkeleton: React.FC = () => {
  return (
    <section
      className="dashboard-container"
      aria-busy="true"
      aria-label="Loading train dashboard"
    >
      {/* Top bar skeleton */}
      <div className="dashboard-top-bar">
        <div className="skeleton-block skeleton-line-sm" style={{ width: '140px' }} />
      </div>

      {/* 1. Train Header Skeleton */}
      <div className="card train-header-card">
        <div className="train-identity">
          <div className="skeleton-block" style={{ width: '76px', height: '42px' }} />
          <div style={{ flex: 1 }}>
            <div
              className="skeleton-block skeleton-line-lg"
              style={{ width: '240px', marginBottom: '8px' }}
            />
            <div className="skeleton-block skeleton-line-sm" style={{ width: '180px' }} />
          </div>
        </div>
      </div>

      {/* 2. Current Position Skeleton */}
      <div className="card current-position-card">
        <div className="card-header-row">
          <div className="skeleton-block skeleton-line-sm" style={{ width: '130px' }} />
          <div className="skeleton-block skeleton-line-sm" style={{ width: '110px' }} />
        </div>
        <div className="segment-stations-row">
          <div className="skeleton-block skeleton-line-md" style={{ width: '100px' }} />
          <div className="skeleton-block skeleton-line-sm" style={{ width: '140px' }} />
          <div className="skeleton-block skeleton-line-md" style={{ width: '100px' }} />
        </div>
        <div
          className="skeleton-block"
          style={{ width: '100%', height: '10px', marginTop: '12px' }}
        />
      </div>

      {/* 3. ETA + Delay Skeleton */}
      <div className="dashboard-metrics-grid">
        <div className="card">
          <div
            className="skeleton-block skeleton-line-sm"
            style={{ width: '140px', marginBottom: '16px' }}
          />
          <div
            className="skeleton-block"
            style={{ width: '160px', height: '38px', marginBottom: '12px' }}
          />
          <div className="skeleton-block skeleton-line-sm" style={{ width: '210px' }} />
        </div>
        <div className="card">
          <div
            className="skeleton-block skeleton-line-sm"
            style={{ width: '120px', marginBottom: '16px' }}
          />
          <div
            className="skeleton-block"
            style={{ width: '130px', height: '38px', marginBottom: '12px' }}
          />
          <div className="skeleton-block skeleton-line-sm" style={{ width: '220px' }} />
        </div>
      </div>

      {/* 4. Two-Column Breakdown & Upcoming Stations Skeleton */}
      <div className="dashboard-two-col-grid">
        <div className="card">
          <div
            className="skeleton-block skeleton-line-sm"
            style={{ width: '150px', marginBottom: '18px' }}
          />
          {[1, 2, 3, 4, 5].map((n) => (
            <div
              key={n}
              className="skeleton-block skeleton-line-sm"
              style={{ width: '100%', marginBottom: '12px' }}
            />
          ))}
        </div>
        <div className="card">
          <div
            className="skeleton-block skeleton-line-sm"
            style={{ width: '160px', marginBottom: '18px' }}
          />
          {[1, 2, 3, 4].map((n) => (
            <div
              key={n}
              className="skeleton-block skeleton-line-md"
              style={{ width: '100%', marginBottom: '14px' }}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

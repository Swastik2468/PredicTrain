import React from 'react';
import { CurrentState } from '../types/train';

interface CurrentPositionProps {
  currentState: CurrentState;
}

/**
 * Displays the train's live segment position between currentStation and nextStation,
 * using the authoritative progressPercentage provided by the backend.
 */
export const CurrentPosition: React.FC<CurrentPositionProps> = ({
  currentState,
}) => {
  const clampedProgress = Math.max(
    0,
    Math.min(100, currentState.progressPercentage)
  );

  return (
    <div className="card current-position-card">
      <div className="card-header-row">
        <span className="card-section-label">CURRENT POSITION</span>
        <span className="timestamp-text">
          Last updated: {currentState.lastUpdated}
        </span>
      </div>

      <div className="segment-stations-row">
        <div className="segment-endpoint">
          <span className="endpoint-caption">Current Station</span>
          <span className="endpoint-name">{currentState.currentStation}</span>
        </div>

        <div className="segment-progress-badge">
          {clampedProgress}% of segment completed
        </div>

        <div className="segment-endpoint segment-endpoint-right">
          <span className="endpoint-caption">Next Station</span>
          <span className="endpoint-name">{currentState.nextStation}</span>
        </div>
      </div>

      <div
        className="segment-track-container"
        role="progressbar"
        aria-valuenow={clampedProgress}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-label={`Train progress from ${currentState.currentStation} to ${currentState.nextStation}`}
      >
        <div className="segment-track-line">
          <div
            className="segment-track-fill"
            style={{ width: `${clampedProgress}%` }}
          />
          <div
            className="segment-train-marker"
            style={{ left: `${clampedProgress}%` }}
            title={`${clampedProgress}%`}
          >
            <span className="marker-dot" />
            <span className="marker-percentage">{clampedProgress}%</span>
          </div>
        </div>
      </div>
    </div>
  );
};

import React from 'react';
import { Link } from 'react-router-dom';
import { TrainInfo } from '../types/train';

interface TrainHeaderProps {
  train: TrainInfo;
  isRefreshing?: boolean;
  onManualRefresh?: () => void;
}

/**
 * Displays train number, train name, source -> destination, and the
 * "<- Search another train" navigation action.
 */
export const TrainHeader: React.FC<TrainHeaderProps> = ({
  train,
  isRefreshing = false,
  onManualRefresh,
}) => {
  return (
    <div className="train-header-wrapper">
      <div className="dashboard-top-bar">
        <Link to="/" className="back-link">
          &larr; Search another train
        </Link>
        <div className="refresh-status-area">
          {isRefreshing && (
            <span className="updating-pill" aria-live="polite">
              <span className="updating-dot" /> Updating...
            </span>
          )}
          {onManualRefresh && (
            <button
              type="button"
              className="secondary-button-sm"
              onClick={onManualRefresh}
              disabled={isRefreshing}
            >
              Refresh
            </button>
          )}
        </div>
      </div>

      <div className="card train-header-card">
        <div className="train-identity">
          <span className="train-number-badge">{train.trainNumber}</span>
          <div>
            <h1 className="train-name-title">{train.trainName}</h1>
            <div className="train-corridor">
              <span className="corridor-station">{train.source}</span>
              <span className="corridor-arrow" aria-hidden="true">
                &rarr;
              </span>
              <span className="corridor-station">{train.destination}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

import React from 'react';
import { Link } from 'react-router-dom';
import { DashboardError } from '../types/train';

interface ErrorStateProps {
  error: DashboardError;
  trainNumber: string;
  onRetry: () => void;
}

/**
 * Displays structured, actionable error messages for:
 * - TRAIN NOT FOUND (404)
 * - INVALID INPUT (400)
 * - TIMEOUT
 * - BACKEND UNAVAILABLE
 */
export const ErrorState: React.FC<ErrorStateProps> = ({
  error,
  trainNumber,
  onRetry,
}) => {
  const isNotFound = error.code === 'NOT_FOUND';
  const isInvalidInput = error.code === 'INVALID_INPUT';
  const isTimeout = error.code === 'TIMEOUT';

  let badgeText = 'System Notice';
  let headingText = 'Unable to retrieve live train information.';
  let bodyText = 'Please try again.';

  if (isNotFound) {
    badgeText = '404 • Train Not Found';
    headingText = 'Train not found';
    bodyText = `We couldn't find a train with number ${trainNumber}.`;
  } else if (isInvalidInput) {
    badgeText = '400 • Invalid Train Number';
    headingText = 'Invalid train number';
    bodyText = error.message;
  } else if (isTimeout) {
    badgeText = 'Request Timeout';
    headingText = 'Unable to retrieve the latest train information.';
    bodyText = 'The request took too long to respond. Please try again.';
  } else {
    badgeText = 'Service Unavailable';
    headingText = 'Unable to retrieve live train information.';
    bodyText = 'Please try again.';
  }

  const canRetry = !isNotFound && !isInvalidInput;

  return (
    <div className="card error-state-card" role="alert">
      <span className="error-badge">{badgeText}</span>
      <h1 className="page-title">{headingText}</h1>
      <p className="page-subtitle">{bodyText}</p>

      <div className="error-actions">
        {canRetry && (
          <button
            type="button"
            className="primary-button"
            onClick={onRetry}
          >
            Retry
          </button>
        )}
        <Link
          to="/"
          className={
            canRetry ? 'secondary-button button-link' : 'primary-button button-link'
          }
        >
          Search another train
        </Link>
      </div>
    </div>
  );
};

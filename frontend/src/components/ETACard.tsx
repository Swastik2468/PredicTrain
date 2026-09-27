import React from 'react';
import { ETA } from '../types/train';

interface ETACardProps {
  eta: ETA;
  lastCalculated: string;
}

/**
 * Formats a duration in minutes into hours and minutes (e.g. "3h 51m remaining").
 * Note: This only formats the backend's authoritative remainingMinutes integer;
 * it does NOT calculate or alter the ETA.
 */
export function formatHoursAndMinutes(totalMinutes: number): string {
  if (totalMinutes <= 0) {
    return '0h 00m';
  }
  const hours = Math.floor(totalMinutes / 60);
  const mins = totalMinutes % 60;
  if (hours === 0) {
    return `${mins}m`;
  }
  const paddedMins = mins < 10 ? `0${mins}` : `${mins}`;
  return `${hours}h ${paddedMins}m`;
}

/**
 * Displays the authoritative predicted destination arrival time and remaining travel time in hour format.
 */
export const ETACard: React.FC<ETACardProps> = ({ eta, lastCalculated }) => {
  return (
    <div className="card eta-card">
      <div className="card-header-row">
        <span className="card-section-label">EXPECTED ARRIVAL</span>
        <span className="dynamic-eta-badge">Dynamic ETA</span>
      </div>

      <div className="eta-primary-value">{eta.estimatedArrival}</div>

      <div className="eta-destination-row">
        <span className="eta-destination-name">{eta.destination}</span>
        <span className="eta-remaining-pill">
          {eta.remainingMinutes <= 0
            ? 'Arrived at destination'
            : `${formatHoursAndMinutes(eta.remainingMinutes)} remaining`}
        </span>
      </div>

      <div className="card-footer-meta">
        Last calculated: {lastCalculated}
      </div>
    </div>
  );
};

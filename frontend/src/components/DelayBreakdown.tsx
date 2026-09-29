import React, { useState } from 'react';
import { DelayBreakdownData } from '../types/train';
import { formatHoursAndMinutes } from './ETACard';

interface DelayBreakdownProps {
  breakdown: DelayBreakdownData;
  scheduledArrival: string;
}

/**
 * Collapsible dropdown displaying the explainable ETA contribution breakdown:
 * - Historical running time
 * - Expected station dwell
 * - Weather delay
 * - Incident/congestion
 * - ML correction
 * - Official IRCTC scheduled arrival
 * - Total remaining travel time (in hours and minutes format)
 */
export const DelayBreakdown: React.FC<DelayBreakdownProps> = ({
  breakdown,
  scheduledArrival,
}) => {
  const [isOpen, setIsOpen] = useState<boolean>(true);

  const formatAdditive = (mins: number) =>
    mins > 0 ? `+${formatHoursAndMinutes(mins)}` : `${mins}m`;

  const mlDisplay =
    breakdown.mlCorrectionMinutes === null ||
    breakdown.mlCorrectionMinutes === undefined
      ? 'Unavailable'
      : breakdown.mlCorrectionMinutes > 0
      ? `+${formatHoursAndMinutes(breakdown.mlCorrectionMinutes)}`
      : `${breakdown.mlCorrectionMinutes}m`;

  return (
    <div className="card breakdown-card">
      <button
        type="button"
        className="explanation-toggle-btn"
        onClick={() => setIsOpen((prev) => !prev)}
        aria-expanded={isOpen}
      >
        <div className="breakdown-toggle-header">
          <span className="explanation-toggle-title">ETA Contribution</span>
          <span className="card-section-sub">Layered Forecast Model</span>
        </div>
        <span className="explanation-chevron" aria-hidden="true">
          {isOpen ? '▲' : '▼'}
        </span>
      </button>

      {isOpen && (
        <div className="explanation-content">
          <div className="breakdown-rows">
            <div className="breakdown-row">
              <span className="breakdown-label">Historical running time</span>
              <span className="breakdown-value">
                {formatHoursAndMinutes(breakdown.baselineRunningMinutes)}
              </span>
            </div>

            <div className="breakdown-row">
              <span className="breakdown-label">Expected station dwell</span>
              <span className="breakdown-value">
                {formatHoursAndMinutes(breakdown.dwellMinutes)}
              </span>
            </div>

            <div className="breakdown-row">
              <span className="breakdown-label">Weather delay</span>
              <span
                className={`breakdown-value ${
                  breakdown.weatherDelayMinutes > 0 ? 'value-warning' : ''
                }`}
              >
                {formatAdditive(breakdown.weatherDelayMinutes)}
              </span>
            </div>

            <div className="breakdown-row">
              <span className="breakdown-label">Incident/congestion</span>
              <span
                className={`breakdown-value ${
                  breakdown.incidentDelayMinutes > 0 ? 'value-danger' : ''
                }`}
              >
                {formatAdditive(breakdown.incidentDelayMinutes)}
              </span>
            </div>

            <div className="breakdown-row">
              <span className="breakdown-label">
                ML correction{' '}
                <span className="optional-tag">
                  {breakdown.mlCorrectionMinutes === 0
                    ? '(Disabled)'
                    : '(Optional)'}
                </span>
              </span>
              <span className="breakdown-value value-muted">{mlDisplay}</span>
            </div>

            <div className="breakdown-divider" />

            <div className="breakdown-row">
              <span className="breakdown-label">
                Official IRCTC arrival{' '}
                <span className="optional-tag">(Scheduled)</span>
              </span>
              <span className="breakdown-value">{scheduledArrival}</span>
            </div>

            <div className="breakdown-row breakdown-total-row">
              <span className="breakdown-total-label">
                Remaining travel time
              </span>
              <span className="breakdown-total-value">
                {formatHoursAndMinutes(breakdown.totalRemainingMinutes)}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

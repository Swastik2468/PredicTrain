import React from 'react';

interface DelayCardProps {
  currentDelayMinutes: number;
}

/**
 * Displays the train's total predicted delay (weather + incident/congestion + ML).
 */
export const DelayCard: React.FC<DelayCardProps> = ({
  currentDelayMinutes,
}) => {
  const isDelayed = currentDelayMinutes > 0;
  const isEarly = currentDelayMinutes < 0;

  let statusClass = 'status-ontime';
  let statusBadgeText = 'Running on schedule';
  let displayValue = 'On time';

  if (isDelayed) {
    statusClass = currentDelayMinutes >= 15 ? 'status-alert' : 'status-delay';
    statusBadgeText = 'Currently delayed';
    displayValue = `+${currentDelayMinutes} min`;
  } else if (isEarly) {
    statusClass = 'status-ontime';
    statusBadgeText = 'Running early';
    displayValue = `${currentDelayMinutes} min`;
  }

  return (
    <div className={`card delay-card ${statusClass}`}>
      <div className="card-header-row">
        <span className="card-section-label">CURRENT DELAY</span>
        <span className={`delay-status-pill ${statusClass}`}>
          {statusBadgeText}
        </span>
      </div>

      <div className="delay-primary-value">{displayValue}</div>

      <p className="delay-footnote">
        Total predicted delay from weather and route conditions.
      </p>
    </div>
  );
};

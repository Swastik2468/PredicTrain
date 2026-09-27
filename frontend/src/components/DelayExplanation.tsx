import React, { useState } from 'react';
import { Explanation, ExplanationCategory } from '../types/train';

interface DelayExplanationProps {
  explanations: Explanation[];
}

function getCategoryLabel(type: ExplanationCategory): string {
  switch (type) {
    case 'WEATHER':
      return 'Weather';
    case 'CONGESTION':
      return 'Congestion';
    case 'TRACK_FAULT':
      return 'Track Fault';
    case 'ACCIDENT':
      return 'Operational Incident';
    case 'SIGNAL_FAILURE':
      return 'Signal Failure';
    case 'TEMPORARY_RESTRICTION':
      return 'Temporary Speed Restriction';
    default:
      return 'Operational Factor';
  }
}

/**
 * Expandable/collapsible "Why is my train delayed?" section (Section 15).
 */
export const DelayExplanation: React.FC<DelayExplanationProps> = ({
  explanations,
}) => {
  const [isOpen, setIsOpen] = useState<boolean>(true);

  return (
    <div className="card explanation-card">
      <button
        type="button"
        className="explanation-toggle-btn"
        onClick={() => setIsOpen((prev) => !prev)}
        aria-expanded={isOpen}
      >
        <span className="explanation-toggle-title">
          Why is my train delayed?
        </span>
        <span className="explanation-chevron" aria-hidden="true">
          {isOpen ? '▲' : '▼'}
        </span>
      </button>

      {isOpen && (
        <div className="explanation-content">
          {explanations.length === 0 ? (
            <p className="helper-text">
              No active weather or operational incident delays along the
              remaining route.
            </p>
          ) : (
            <div className="explanation-list">
              {explanations.map((item, idx) => (
                <div key={idx} className="explanation-item">
                  <div className="explanation-item-header">
                    <span className="cause-title">
                      {item.title || getCategoryLabel(item.type)}
                    </span>
                    {item.delayMinutes !== undefined &&
                      item.delayMinutes > 0 && (
                        <span className="cause-impact">
                          Estimated impact: +{item.delayMinutes} minutes
                        </span>
                      )}
                  </div>
                  <p className="explanation-description">{item.description}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

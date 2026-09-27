import React from 'react';
import { RouteStop } from '../types/train';

interface RouteTimelineProps {
  route: RouteStop[];
}

/**
 * Full route station timeline visualization.
 * Clearly distinguishes completed stations, current station, next station,
 * upcoming stations, and final destination without requiring GPS.
 */
export const RouteTimeline: React.FC<RouteTimelineProps> = ({ route }) => {
  return (
    <div className="card route-timeline-card">
      <div className="card-header-row">
        <span className="card-section-label">ROUTE OVERVIEW</span>
        <div className="route-legend">
          <span className="legend-item">
            <span className="legend-dot legend-completed" /> Completed
          </span>
          <span className="legend-item">
            <span className="legend-dot legend-current" /> Current
          </span>
          <span className="legend-item">
            <span className="legend-dot legend-next" /> Next
          </span>
          <span className="legend-item">
            <span className="legend-dot legend-upcoming" /> Upcoming
          </span>
        </div>
      </div>

      <ul className="route-stop-list">
        {route.map((stop, idx) => {
          const isLast = idx === route.length - 1;
          const statusLower = stop.status.toLowerCase();

          return (
            <li
              key={`${stop.station}-${idx}`}
              className={`route-stop-item route-stop-${statusLower}`}
            >
              <div className="route-node-col">
                <span className={`route-node-circle circle-${statusLower}`} />
                {!isLast && (
                  <span
                    className={`route-node-line ${
                      stop.status === 'COMPLETED' ? 'line-completed' : ''
                    }`}
                  />
                )}
              </div>

              <div className="route-stop-body">
                <div className="route-stop-left">
                  <span className="route-stop-name">{stop.station}</span>
                  {stop.status === 'CURRENT' && (
                    <span className="route-badge badge-current">
                      CURRENT STATION
                    </span>
                  )}
                  {stop.status === 'NEXT' && (
                    <span className="route-badge badge-next">
                      &larr; NEXT
                    </span>
                  )}
                  {stop.status === 'DESTINATION' && (
                    <span className="route-badge badge-destination">
                      DESTINATION
                    </span>
                  )}
                </div>

                {stop.estimatedArrival && (
                  <span className="route-stop-eta">
                    ETA {stop.estimatedArrival}
                  </span>
                )}
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  );
};

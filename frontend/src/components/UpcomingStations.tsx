import React from 'react';
import { StationETA } from '../types/train';

interface UpcomingStationsProps {
  upcomingStations: StationETA[];
}

/**
 * Displays the upcoming stations along the train's remaining route as a
 * railway-style vertical timeline, highlighting the immediate next station.
 * Preserves the exact station order returned by the backend.
 */
export const UpcomingStations: React.FC<UpcomingStationsProps> = ({
  upcomingStations,
}) => {
  return (
    <div className="card upcoming-stations-card">
      <div className="card-header-row">
        <span className="card-section-label">UPCOMING STATIONS</span>
        <span className="card-section-sub">
          {upcomingStations.length} stop{upcomingStations.length === 1 ? '' : 's'}{' '}
          ahead
        </span>
      </div>

      {upcomingStations.length === 0 ? (
        <p className="helper-text">
          Train has reached its final destination.
        </p>
      ) : (
        <ul className="station-timeline-list">
          {upcomingStations.map((stop, idx) => {
            const isNextStop = idx === 0;
            const isFinalDestination = idx === upcomingStations.length - 1;

            return (
              <li
                key={`${stop.station}-${idx}`}
                className={`station-timeline-item ${
                  isNextStop ? 'station-item-next' : ''
                }`}
              >
                <div className="station-node-column">
                  <span
                    className={`station-node-dot ${
                      isNextStop
                        ? 'dot-next'
                        : isFinalDestination
                        ? 'dot-destination'
                        : ''
                    }`}
                  />
                  {!isFinalDestination && (
                    <span className="station-node-connector" />
                  )}
                </div>

                <div className="station-content-column">
                  <div className="station-row-main">
                    <div className="station-title-group">
                      <span className="station-name-text">{stop.station}</span>
                      {isNextStop && (
                        <span className="next-stop-tag">Next Stop</span>
                      )}
                      {!isNextStop && isFinalDestination && (
                        <span className="dest-stop-tag">Destination</span>
                      )}
                    </div>

                    <div className="station-eta-group">
                      <span className="station-eta-time">
                        ETA {stop.estimatedArrival}
                      </span>
                      {stop.delayMinutes !== undefined &&
                        stop.delayMinutes > 0 && (
                          <span className="station-delay-tag">
                            +{stop.delayMinutes} min
                          </span>
                        )}
                    </div>
                  </div>
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
};

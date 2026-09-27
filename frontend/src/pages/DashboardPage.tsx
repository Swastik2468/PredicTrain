import React from 'react';
import { useParams } from 'react-router-dom';
import { useTrainDashboard } from '../hooks/useTrainDashboard';
import { TrainHeader } from '../components/TrainHeader';
import { CurrentPosition } from '../components/CurrentPosition';
import { ETACard } from '../components/ETACard';
import { DelayCard } from '../components/DelayCard';
import { DelayBreakdown } from '../components/DelayBreakdown';
import { DelayExplanation } from '../components/DelayExplanation';
import { UpcomingStations } from '../components/UpcomingStations';
import { RouteTimeline } from '../components/RouteTimeline';
import { LoadingSkeleton } from '../components/LoadingSkeleton';
import { ErrorState } from '../components/ErrorState';

/**
 * DashboardPage connected to GET /api/trains/{trainNumber}/dashboard
 * with LoadingSkeleton and ErrorState handling.
 */
export const DashboardPage: React.FC = () => {
  const { trainNumber = '' } = useParams<{ trainNumber: string }>();
  const { data, loading, isRefreshing, error, refresh } =
    useTrainDashboard(trainNumber);

  if (loading && !data) {
    return <LoadingSkeleton />;
  }

  if (error || !data) {
    return (
      <section className="dashboard-container">
        <ErrorState
          error={
            error || {
              code: 'BACKEND_UNAVAILABLE',
              message: 'Unable to retrieve live train information.',
            }
          }
          trainNumber={trainNumber}
          onRetry={refresh}
        />
      </section>
    );
  }

  return (
    <section className="dashboard-container">
      {/* 1. Train Header */}
      <TrainHeader
        train={data.train}
        isRefreshing={isRefreshing}
        onManualRefresh={refresh}
      />

      {/* 2. Current Position */}
      <CurrentPosition currentState={data.currentState} />

      {/* 3. ETA + 4. Current Delay (Top Metric Row) */}
      <div className="dashboard-metrics-grid">
        <ETACard
          eta={data.eta}
          lastCalculated={data.currentState.lastUpdated}
        />
        <DelayCard
          currentDelayMinutes={data.currentState.currentDelayMinutes}
        />
      </div>

      {/* 5. Delay Breakdown + Why is my train delayed? */}
      <div className="dashboard-two-col-grid">
        <div className="dashboard-col-stack">
          <DelayBreakdown breakdown={data.delayBreakdown} />
          <DelayExplanation explanations={data.explanations} />
        </div>

        {/* 6. Upcoming Stations + 7. Route Timeline */}
        <div className="dashboard-col-stack">
          <UpcomingStations upcomingStations={data.upcomingStations} />
          <RouteTimeline route={data.routeTimeline} />
        </div>
      </div>
    </section>
  );
};

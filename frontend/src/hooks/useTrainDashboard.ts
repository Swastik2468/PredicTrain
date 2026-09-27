import { useCallback, useEffect, useRef, useState } from 'react';
import { getTrainDashboard } from '../api/trainApi';
import { DASHBOARD_REFRESH_INTERVAL } from '../config/config';
import { DashboardData, DashboardError } from '../types/train';

interface UseTrainDashboardResult {
  data: DashboardData | null;
  loading: boolean;
  isRefreshing: boolean;
  error: DashboardError | null;
  refresh: () => Promise<void>;
}

/**
 * Custom hook that manages fetching the train dashboard from
 * GET /api/trains/{trainNumber}/dashboard and automatically polling
 * every 2 minutes (DASHBOARD_REFRESH_INTERVAL = 120000 ms).
 */
export function useTrainDashboard(
  trainNumber: string
): UseTrainDashboardResult {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [isRefreshing, setIsRefreshing] = useState<boolean>(false);
  const [error, setError] = useState<DashboardError | null>(null);

  // Prevent overlapping background requests
  const inFlightRef = useRef<boolean>(false);

  const fetchDashboard = useCallback(
    async (isBackgroundRefresh: boolean) => {
      if (!trainNumber || !trainNumber.trim()) {
        setError({
          code: 'INVALID_INPUT',
          message: 'Please provide a valid train number.',
        });
        setLoading(false);
        return;
      }

      if (inFlightRef.current) {
        return;
      }
      inFlightRef.current = true;

      if (isBackgroundRefresh) {
        setIsRefreshing(true);
      } else {
        setLoading(true);
        setError(null);
      }

      try {
        const result = await getTrainDashboard(trainNumber);
        setData(result);
        setError(null);
      } catch (err: unknown) {
        const dashboardErr = err as DashboardError;
        // During background polling, preserve existing data if already loaded
        if (!isBackgroundRefresh) {
          setError(
            dashboardErr?.code
              ? dashboardErr
              : {
                  code: 'BACKEND_UNAVAILABLE',
                  message:
                    'Unable to retrieve live train information. Please try again.',
                  trainNumber,
                }
          );
        }
      } finally {
        inFlightRef.current = false;
        setLoading(false);
        setIsRefreshing(false);
      }
    },
    [trainNumber]
  );

  // Initial fetch + 2-minute polling interval with cleanup on unmount
  useEffect(() => {
    fetchDashboard(false);

    const intervalId = window.setInterval(() => {
      fetchDashboard(true);
    }, DASHBOARD_REFRESH_INTERVAL);

    return () => {
      window.clearInterval(intervalId);
    };
  }, [fetchDashboard]);

  const refresh = useCallback(async () => {
    await fetchDashboard(true);
  }, [fetchDashboard]);

  return {
    data,
    loading,
    isRefreshing,
    error,
    refresh,
  };
}

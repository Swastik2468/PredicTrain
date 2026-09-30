import axios, { AxiosError } from 'axios';
import { API_BASE_URL, USE_MOCK_API } from '../config/config';
import {
  BackendDashboardResponse,
  BackendExplanation,
  DashboardData,
  DashboardError,
  Explanation,
  ExplanationCategory,
  RouteStop,
  StationETA,
} from '../types/train';
import { MOCK_DASHBOARDS } from './mockTrainData';

/**
 * Dedicated Axios instance configured with VITE_API_BASE_URL.
 * UI components never call Axios directly.
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
  headers: {
    Accept: 'application/json',
  },
});

/**
 * Fetches the full dashboard data for a train from:
 *   GET /api/trains/{trainNumber}/dashboard
 *
 * When VITE_USE_MOCK_API=true, returns pre-calculated mock data instead.
 */
export async function getTrainDashboard(
  trainNumber: string
): Promise<DashboardData> {
  const trimmed = trainNumber.trim();

  if (USE_MOCK_API) {
    // Simulate a brief network latency so loading skeletons are visible in mock mode
    await new Promise((resolve) => setTimeout(resolve, 250));
    const mockData = MOCK_DASHBOARDS[trimmed];
    if (!mockData) {
      const notFoundError: DashboardError = {
        code: 'NOT_FOUND',
        message: `We couldn't find a train with number ${trimmed}.`,
        trainNumber: trimmed,
      };
      throw notFoundError;
    }
    return mockData;
  }

  try {
    const response = await apiClient.get<BackendDashboardResponse>(
      `/api/trains/${encodeURIComponent(trimmed)}/dashboard`
    );
    return mapBackendResponseToDashboardData(response.data, trimmed);
  } catch (err: unknown) {
    throw toDashboardError(err, trimmed);
  }
}

/**
 * Isolated mapper that converts the Spring Boot backend DTO response into
 * the normalized DashboardData model used by React components.
 */
function mapBackendResponseToDashboardData(
  raw: BackendDashboardResponse,
  fallbackTrainNumber: string
): DashboardData {
  const trainNumber =
    raw.train?.trainNumber || raw.train?.number || fallbackTrainNumber;
  const trainName =
    raw.train?.trainName || raw.train?.name || `Train ${trainNumber}`;
  const source = raw.train?.source || 'Origin';
  const destination =
    raw.train?.destination || raw.eta?.destination || 'Destination';

  const currentStation = raw.currentState?.currentStation || source;
  const nextStation = raw.currentState?.nextStation || destination;
  const progressPercentage = raw.currentState?.progressPercentage ?? 0;
  const lastUpdated = raw.currentState?.lastUpdated || 'Just now';

  const remainingMinutes = raw.eta?.remainingMinutes ?? 0;
  const estimatedArrival = raw.eta?.estimatedArrival || '--:--';

  const bd = raw.delayBreakdown;
  const baselineRunningMinutes =
    bd?.baselineRunningMinutes ?? bd?.runningTimeMinutes ?? 0;
  const dwellMinutes = bd?.dwellMinutes ?? bd?.stationDwellMinutes ?? 0;
  const weatherDelayMinutes = bd?.weatherDelayMinutes ?? 0;
  const incidentDelayMinutes = bd?.incidentDelayMinutes ?? 0;
  const mlCorrectionMinutes =
    bd?.mlCorrectionMinutes !== undefined ? bd.mlCorrectionMinutes : 0;
  const totalRemainingMinutes =
    bd?.totalRemainingMinutes ?? remainingMinutes;

  const totalDelayMinutes =
    weatherDelayMinutes + incidentDelayMinutes + (mlCorrectionMinutes ?? 0);
  const currentDelayMinutes = bd
    ? totalDelayMinutes
    : (raw.currentState?.currentDelayMinutes ?? 0);

  const scheduledArrival =
    raw.eta?.scheduledArrival ||
    computeScheduledArrivalFallback(estimatedArrival, currentDelayMinutes);

  const upcomingStations: StationETA[] = (raw.upcomingStations || []).map(
    (s) => ({
      station: s.station,
      estimatedArrival: s.estimatedArrival || s.eta || '--:--',
      delayMinutes: s.delayMinutes,
    })
  );

  const explanations: Explanation[] = (raw.explanations || []).map(
    normalizeExplanation
  );

  const routeTimeline: RouteStop[] = buildRouteTimeline(
    trainNumber,
    source,
    destination,
    currentStation,
    upcomingStations
  );

  return {
    train: {
      trainNumber,
      trainName,
      source,
      destination,
    },
    currentState: {
      currentStation,
      nextStation,
      progressPercentage,
      currentDelayMinutes,
      lastUpdated,
    },
    eta: {
      destination: raw.eta?.destination || destination,
      scheduledArrival,
      estimatedArrival,
      remainingMinutes,
    },
    delayBreakdown: {
      baselineRunningMinutes,
      dwellMinutes,
      weatherDelayMinutes,
      incidentDelayMinutes,
      mlCorrectionMinutes,
      totalRemainingMinutes,
    },
    upcomingStations,
    explanations,
    routeTimeline,
  };
}

/**
 * Fallback helper that derives the official scheduled arrival (HH:mm)
 * by subtracting total delay minutes from estimatedArrival when not explicitly provided.
 */
function computeScheduledArrivalFallback(
  estimatedArrival: string,
  delayMinutes: number
): string {
  const match = estimatedArrival.match(/^(\d{1,2}):(\d{2})$/);
  if (!match) {
    return estimatedArrival;
  }
  const hours = parseInt(match[1], 10);
  const mins = parseInt(match[2], 10);
  const totalMins = ((hours * 60 + mins - delayMinutes) % 1440 + 1440) % 1440;
  const schedHours = String(Math.floor(totalMins / 60)).padStart(2, '0');
  const schedMins = String(totalMins % 60).padStart(2, '0');
  return `${schedHours}:${schedMins}`;
}

/**
 * Converts either a plain string explanation (returned by our Spring Boot backend)
 * or a structured explanation object into the normalized Explanation interface.
 */
function normalizeExplanation(item: BackendExplanation): Explanation {
  if (typeof item === 'string') {
    const category = inferCategoryFromText(item);
    const delayMatch = item.match(/(\d+)\s*minutes?/i);
    const delayMinutes = delayMatch ? parseInt(delayMatch[1], 10) : undefined;
    return {
      type: category.type,
      title: category.title,
      description: item,
      delayMinutes,
    };
  }

  const text = item.description || item.message || '';
  const inferred = inferCategoryFromText(item.type || text);
  return {
    type: inferred.type,
    title: inferred.title,
    description: text,
    delayMinutes: item.delayMinutes,
  };
}

function inferCategoryFromText(text: string): {
  type: ExplanationCategory;
  title: string;
} {
  const upper = text.toUpperCase();
  if (
    upper.includes('WEATHER') ||
    upper.includes('RAIN') ||
    upper.includes('FOG') ||
    upper.includes('STORM')
  ) {
    return { type: 'WEATHER', title: 'Weather' };
  }
  if (upper.includes('CONGESTION')) {
    return { type: 'CONGESTION', title: 'Congestion' };
  }
  if (upper.includes('SIGNAL')) {
    return { type: 'SIGNAL_FAILURE', title: 'Signal Failure' };
  }
  if (upper.includes('TRACK')) {
    return { type: 'TRACK_FAULT', title: 'Track Fault' };
  }
  if (upper.includes('RESTRICTION') || upper.includes('SPEED')) {
    return {
      type: 'TEMPORARY_RESTRICTION',
      title: 'Temporary Speed Restriction',
    };
  }
  if (upper.includes('ACCIDENT')) {
    return { type: 'ACCIDENT', title: 'Operational Incident' };
  }
  return { type: 'OPERATIONAL', title: 'Operational Factor' };
}

/**
 * Builds the full station sequence for RouteTimeline using the backend's
 * currentStation and ordered upcomingStations list.
 */
function buildRouteTimeline(
  trainNumber: string,
  source: string,
  destination: string,
  currentStation: string,
  upcomingStations: StationETA[]
): RouteStop[] {
  const etaMap = new Map<string, string>();
  for (const s of upcomingStations) {
    etaMap.set(s.station.toLowerCase(), s.estimatedArrival);
  }

  // If we have the full station list for this seeded train, overlay live ETAs on it
  const knownMock = MOCK_DASHBOARDS[trainNumber];
  if (knownMock && knownMock.routeTimeline.length > 0) {
    const stops = knownMock.routeTimeline.map((r) => r.station);
    const currentIdx = stops.findIndex(
      (st) => st.toLowerCase() === currentStation.toLowerCase()
    );

    if (currentIdx !== -1) {
      return stops.map((stationName, idx) => {
        const liveEta = etaMap.get(stationName.toLowerCase());
        if (idx < currentIdx) {
          return { station: stationName, status: 'COMPLETED' };
        }
        if (idx === currentIdx) {
          return { station: stationName, status: 'CURRENT' };
        }
        if (idx === currentIdx + 1) {
          return {
            station: stationName,
            status: 'NEXT',
            estimatedArrival: liveEta,
          };
        }
        if (idx === stops.length - 1) {
          return {
            station: stationName,
            status: 'DESTINATION',
            estimatedArrival: liveEta,
          };
        }
        return {
          station: stationName,
          status: 'UPCOMING',
          estimatedArrival: liveEta,
        };
      });
    }
  }

  // Generic route timeline constructed directly from backend response
  const result: RouteStop[] = [];
  if (source.toLowerCase() !== currentStation.toLowerCase()) {
    result.push({ station: source, status: 'COMPLETED' });
  }
  result.push({ station: currentStation, status: 'CURRENT' });

  upcomingStations.forEach((u, idx) => {
    const isLast = idx === upcomingStations.length - 1;
    if (idx === 0) {
      result.push({
        station: u.station,
        status: 'NEXT',
        estimatedArrival: u.estimatedArrival,
      });
    } else if (isLast || u.station.toLowerCase() === destination.toLowerCase()) {
      result.push({
        station: u.station,
        status: 'DESTINATION',
        estimatedArrival: u.estimatedArrival,
      });
    } else {
      result.push({
        station: u.station,
        status: 'UPCOMING',
        estimatedArrival: u.estimatedArrival,
      });
    }
  });

  return result;
}

/**
 * Translates Axios errors into a strongly-typed DashboardError for ErrorState.tsx.
 */
function toDashboardError(
  err: unknown,
  trainNumber: string
): DashboardError {
  if (
    err &&
    typeof err === 'object' &&
    'code' in err &&
    'message' in err &&
    !axios.isAxiosError(err)
  ) {
    return err as DashboardError;
  }

  if (axios.isAxiosError(err)) {
    const axiosErr = err as AxiosError<{ message?: string }>;

    if (axiosErr.code === 'ECONNABORTED') {
      return {
        code: 'TIMEOUT',
        message: 'Unable to retrieve the latest train information.',
        trainNumber,
      };
    }

    if (axiosErr.response) {
      const status = axiosErr.response.status;
      const serverMsg = axiosErr.response.data?.message;

      if (status === 404) {
        return {
          code: 'NOT_FOUND',
          message:
            serverMsg || `We couldn't find a train with number ${trainNumber}.`,
          trainNumber,
        };
      }

      if (status === 400) {
        return {
          code: 'INVALID_INPUT',
          message:
            serverMsg ||
            `Invalid train number '${trainNumber}'. Please enter a valid 5-digit train number.`,
          trainNumber,
        };
      }
    }
  }

  return {
    code: 'BACKEND_UNAVAILABLE',
    message: 'Unable to retrieve live train information. Please try again.',
    trainNumber,
  };
}

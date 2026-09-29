/**
 * Strongly-typed TypeScript interfaces for PredicTrack.
 *
 * Separates:
 * 1. Raw Backend API DTO interfaces (BackendDashboardResponse, etc.)
 * 2. Normalized Frontend UI interfaces (DashboardData, etc.)
 *
 * This keeps API field mapping isolated inside src/api/trainApi.ts so UI components
 * never depend on raw backend JSON field names.
 */

// ============================================================================
// 1. Raw Spring Boot Backend Response DTO Types
// ============================================================================

export interface BackendTrainInfo {
  number?: string;
  trainNumber?: string;
  name?: string;
  trainName?: string;
  source: string;
  destination: string;
}

export interface BackendCurrentState {
  currentStation: string;
  nextStation: string;
  progressPercentage: number;
  currentDelayMinutes: number;
  lastUpdated: string;
}

export interface BackendETA {
  destination: string;
  scheduledArrival?: string;
  estimatedArrival: string;
  remainingMinutes: number;
}

export interface BackendDelayBreakdown {
  runningTimeMinutes?: number;
  baselineRunningMinutes?: number;
  stationDwellMinutes?: number;
  dwellMinutes?: number;
  weatherDelayMinutes?: number;
  incidentDelayMinutes?: number;
  mlCorrectionMinutes?: number | null;
  totalRemainingMinutes?: number;
}

export interface BackendStationETA {
  station: string;
  eta?: string;
  estimatedArrival?: string;
  delayMinutes?: number;
}

export interface BackendExplanationObject {
  type?: string;
  description?: string;
  message?: string;
  delayMinutes?: number;
}

export type BackendExplanation = string | BackendExplanationObject;

export interface BackendDashboardResponse {
  train: BackendTrainInfo;
  currentState: BackendCurrentState;
  eta: BackendETA;
  delayBreakdown?: BackendDelayBreakdown;
  upcomingStations?: BackendStationETA[];
  explanations?: BackendExplanation[];
}

// ============================================================================
// 2. Normalized Frontend UI Models
// ============================================================================

export type ExplanationCategory =
  | 'WEATHER'
  | 'CONGESTION'
  | 'TRACK_FAULT'
  | 'ACCIDENT'
  | 'SIGNAL_FAILURE'
  | 'TEMPORARY_RESTRICTION'
  | 'OPERATIONAL';

export interface TrainInfo {
  trainNumber: string;
  trainName: string;
  source: string;
  destination: string;
}

export interface CurrentState {
  currentStation: string;
  nextStation: string;
  progressPercentage: number;
  currentDelayMinutes: number;
  lastUpdated: string;
}

export interface ETA {
  destination: string;
  scheduledArrival: string;
  estimatedArrival: string;
  remainingMinutes: number;
}

export interface DelayBreakdownData {
  baselineRunningMinutes: number;
  dwellMinutes: number;
  weatherDelayMinutes: number;
  incidentDelayMinutes: number;
  mlCorrectionMinutes: number | null;
  totalRemainingMinutes: number;
}

export interface StationETA {
  station: string;
  estimatedArrival: string;
  delayMinutes?: number;
}

export interface Explanation {
  type: ExplanationCategory;
  title: string;
  description: string;
  delayMinutes?: number;
}

export type RouteStationStatus =
  | 'COMPLETED'
  | 'CURRENT'
  | 'NEXT'
  | 'UPCOMING'
  | 'DESTINATION';

export interface RouteStop {
  station: string;
  status: RouteStationStatus;
  estimatedArrival?: string;
}

export interface DashboardData {
  train: TrainInfo;
  currentState: CurrentState;
  eta: ETA;
  delayBreakdown: DelayBreakdownData;
  upcomingStations: StationETA[];
  explanations: Explanation[];
  routeTimeline: RouteStop[];
}

// ============================================================================
// 3. Strongly-Typed API Error Model
// ============================================================================

export type DashboardErrorCode =
  | 'NOT_FOUND'
  | 'INVALID_INPUT'
  | 'TIMEOUT'
  | 'BACKEND_UNAVAILABLE';

export interface DashboardError {
  code: DashboardErrorCode;
  message: string;
  trainNumber?: string;
}

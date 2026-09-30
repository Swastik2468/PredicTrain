/**
 * Centralized frontend configuration for PredicTrack.
 */
const DEFAULT_API_BASE_URL = 'https://predictrain.onrender.com';

export const API_BASE_URL: string = (
  import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL
).replace(/\/+$/, '');

export const USE_MOCK_API: boolean =
  import.meta.env.VITE_USE_MOCK_API === 'true';

/**
 * Polling interval for refreshing the train dashboard (2 minutes = 120,000 ms).
 */
export const DASHBOARD_REFRESH_INTERVAL = 120000;

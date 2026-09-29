import { DashboardData } from '../types/train';

/**
 * Static mock dashboard payloads representing already-calculated responses
 * from the Spring Boot backend.
 * Contains zero ETA calculation logic on the frontend.
 */
export const MOCK_DASHBOARDS: Record<string, DashboardData> = {
  '12901': {
    train: {
      trainNumber: '12901',
      trainName: 'Mumbai-Ahmedabad Express',
      source: 'Mumbai Central',
      destination: 'Ahmedabad',
    },
    currentState: {
      currentStation: 'Surat',
      nextStation: 'Bharuch',
      progressPercentage: 40,
      currentDelayMinutes: 23,
      lastUpdated: '14:05',
    },
    eta: {
      destination: 'Ahmedabad',
      scheduledArrival: '17:33',
      estimatedArrival: '17:56',
      remainingMinutes: 231,
    },
    delayBreakdown: {
      baselineRunningMinutes: 198,
      dwellMinutes: 10,
      weatherDelayMinutes: 11,
      incidentDelayMinutes: 12,
      mlCorrectionMinutes: 0,
      totalRemainingMinutes: 231,
    },
    upcomingStations: [
      { station: 'Bharuch', estimatedArrival: '14:38' },
      { station: 'Vadodara', estimatedArrival: '16:03', delayMinutes: 20 },
      { station: 'Anand', estimatedArrival: '16:49', delayMinutes: 23 },
      { station: 'Ahmedabad', estimatedArrival: '17:56', delayMinutes: 23 },
    ],
    explanations: [
      {
        type: 'WEATHER',
        title: 'Weather',
        description: 'Heavy rain near Vadodara may add approximately 8 minutes.',
        delayMinutes: 8,
      },
      {
        type: 'CONGESTION',
        title: 'Congestion',
        description: 'Congestion between Bharuch and Vadodara may add approximately 12 minutes.',
        delayMinutes: 12,
      },
      {
        type: 'WEATHER',
        title: 'Weather',
        description: 'Moderate rain between Vadodara and Anand may add approximately 3 minutes.',
        delayMinutes: 3,
      },
    ],
    routeTimeline: [
      { station: 'Mumbai Central', status: 'COMPLETED' },
      { station: 'Dadar', status: 'COMPLETED' },
      { station: 'Borivali', status: 'COMPLETED' },
      { station: 'Vapi', status: 'COMPLETED' },
      { station: 'Valsad', status: 'COMPLETED' },
      { station: 'Surat', status: 'CURRENT' },
      { station: 'Bharuch', status: 'NEXT', estimatedArrival: '14:38' },
      { station: 'Vadodara', status: 'UPCOMING', estimatedArrival: '16:03' },
      { station: 'Anand', status: 'UPCOMING', estimatedArrival: '16:49' },
      { station: 'Ahmedabad', status: 'DESTINATION', estimatedArrival: '17:56' },
    ],
  },

  '12951': {
    train: {
      trainNumber: '12951',
      trainName: 'Mumbai-New Delhi Rajdhani Express',
      source: 'Mumbai Central',
      destination: 'New Delhi',
    },
    currentState: {
      currentStation: 'Ratlam',
      nextStation: 'Kota',
      progressPercentage: 50,
      currentDelayMinutes: 6,
      lastUpdated: '14:35',
    },
    eta: {
      destination: 'New Delhi',
      scheduledArrival: '21:11',
      estimatedArrival: '21:17',
      remainingMinutes: 402,
    },
    delayBreakdown: {
      baselineRunningMinutes: 382,
      dwellMinutes: 14,
      weatherDelayMinutes: 6,
      incidentDelayMinutes: 0,
      mlCorrectionMinutes: 0,
      totalRemainingMinutes: 402,
    },
    upcomingStations: [
      { station: 'Kota', estimatedArrival: '16:03' },
      { station: 'Sawai Madhopur', estimatedArrival: '17:18' },
      { station: 'Mathura', estimatedArrival: '19:36', delayMinutes: 6 },
      { station: 'New Delhi', estimatedArrival: '21:17', delayMinutes: 6 },
    ],
    explanations: [
      {
        type: 'WEATHER',
        title: 'Weather',
        description: 'Fog and low visibility near Mathura may add approximately 6 minutes.',
        delayMinutes: 6,
      },
    ],
    routeTimeline: [
      { station: 'Mumbai Central', status: 'COMPLETED' },
      { station: 'Borivali', status: 'COMPLETED' },
      { station: 'Surat', status: 'COMPLETED' },
      { station: 'Vadodara', status: 'COMPLETED' },
      { station: 'Ratlam', status: 'CURRENT' },
      { station: 'Kota', status: 'NEXT', estimatedArrival: '16:03' },
      { station: 'Sawai Madhopur', status: 'UPCOMING', estimatedArrival: '17:18' },
      { station: 'Mathura', status: 'UPCOMING', estimatedArrival: '19:36' },
      { station: 'New Delhi', status: 'DESTINATION', estimatedArrival: '21:17' },
    ],
  },

  '12002': {
    train: {
      trainNumber: '12002',
      trainName: 'New Delhi-Bhopal Shatabdi Express',
      source: 'New Delhi',
      destination: 'Rani Kamalapati',
    },
    currentState: {
      currentStation: 'Agra Cantt',
      nextStation: 'Dholpur',
      progressPercentage: 25,
      currentDelayMinutes: 15,
      lastUpdated: '08:15',
    },
    eta: {
      destination: 'Rani Kamalapati',
      scheduledArrival: '14:08',
      estimatedArrival: '14:23',
      remainingMinutes: 368,
    },
    delayBreakdown: {
      baselineRunningMinutes: 331,
      dwellMinutes: 22,
      weatherDelayMinutes: 0,
      incidentDelayMinutes: 15,
      mlCorrectionMinutes: 0,
      totalRemainingMinutes: 368,
    },
    upcomingStations: [
      { station: 'Dholpur', estimatedArrival: '08:41' },
      { station: 'Morena', estimatedArrival: '09:05' },
      { station: 'Gwalior', estimatedArrival: '09:37' },
      { station: 'Jhansi', estimatedArrival: '11:05', delayMinutes: 15 },
      { station: 'Bina', estimatedArrival: '12:48', delayMinutes: 15 },
      { station: 'Rani Kamalapati', estimatedArrival: '14:23', delayMinutes: 15 },
    ],
    explanations: [
      {
        type: 'SIGNAL_FAILURE',
        title: 'Signal Failure',
        description: 'Signal failure between Gwalior and Jhansi may add approximately 15 minutes.',
        delayMinutes: 15,
      },
    ],
    routeTimeline: [
      { station: 'New Delhi', status: 'COMPLETED' },
      { station: 'Mathura', status: 'COMPLETED' },
      { station: 'Agra Cantt', status: 'CURRENT' },
      { station: 'Dholpur', status: 'NEXT', estimatedArrival: '08:41' },
      { station: 'Morena', status: 'UPCOMING', estimatedArrival: '09:05' },
      { station: 'Gwalior', status: 'UPCOMING', estimatedArrival: '09:37' },
      { station: 'Jhansi', status: 'UPCOMING', estimatedArrival: '11:05' },
      { station: 'Bina', status: 'UPCOMING', estimatedArrival: '12:48' },
      { station: 'Rani Kamalapati', status: 'DESTINATION', estimatedArrival: '14:23' },
    ],
  },

  '12627': {
    train: {
      trainNumber: '12627',
      trainName: 'Karnataka Express',
      source: 'KSR Bengaluru',
      destination: 'Manmad',
    },
    currentState: {
      currentStation: 'Wadi',
      nextStation: 'Solapur',
      progressPercentage: 60,
      currentDelayMinutes: 2,
      lastUpdated: '14:20',
    },
    eta: {
      destination: 'Manmad',
      scheduledArrival: '21:20',
      estimatedArrival: '21:22',
      remainingMinutes: 422,
    },
    delayBreakdown: {
      baselineRunningMinutes: 407,
      dwellMinutes: 13,
      weatherDelayMinutes: 2,
      incidentDelayMinutes: 0,
      mlCorrectionMinutes: 0,
      totalRemainingMinutes: 422,
    },
    upcomingStations: [
      { station: 'Solapur', estimatedArrival: '15:14', delayMinutes: 2 },
      { station: 'Daund', estimatedArrival: '18:04', delayMinutes: 2 },
      { station: 'Ahmadnagar', estimatedArrival: '19:29', delayMinutes: 2 },
      { station: 'Manmad', estimatedArrival: '21:22', delayMinutes: 2 },
    ],
    explanations: [
      {
        type: 'WEATHER',
        title: 'Weather',
        description: 'Light rain near Solapur may add approximately 2 minutes.',
        delayMinutes: 2,
      },
    ],
    routeTimeline: [
      { station: 'KSR Bengaluru', status: 'COMPLETED' },
      { station: 'Dharmavaram', status: 'COMPLETED' },
      { station: 'Anantapur', status: 'COMPLETED' },
      { station: 'Guntakal', status: 'COMPLETED' },
      { station: 'Wadi', status: 'CURRENT' },
      { station: 'Solapur', status: 'NEXT', estimatedArrival: '15:14' },
      { station: 'Daund', status: 'UPCOMING', estimatedArrival: '18:04' },
      { station: 'Ahmadnagar', status: 'UPCOMING', estimatedArrival: '19:29' },
      { station: 'Manmad', status: 'DESTINATION', estimatedArrival: '21:22' },
    ],
  },

  '12841': {
    train: {
      trainNumber: '12841',
      trainName: 'Coromandel Express',
      source: 'Shalimar',
      destination: 'MGR Chennai Central',
    },
    currentState: {
      currentStation: 'Bhubaneswar',
      nextStation: 'Brahmapur',
      progressPercentage: 30,
      currentDelayMinutes: 20,
      lastUpdated: '15:10',
    },
    eta: {
      destination: 'MGR Chennai Central',
      scheduledArrival: '06:55',
      estimatedArrival: '07:15',
      remainingMinutes: 965,
    },
    delayBreakdown: {
      baselineRunningMinutes: 912,
      dwellMinutes: 33,
      weatherDelayMinutes: 10,
      incidentDelayMinutes: 10,
      mlCorrectionMinutes: 0,
      totalRemainingMinutes: 965,
    },
    upcomingStations: [
      { station: 'Brahmapur', estimatedArrival: '16:38' },
      { station: 'Visakhapatnam', estimatedArrival: '20:23', delayMinutes: 10 },
      { station: 'Rajahmundry', estimatedArrival: '23:23', delayMinutes: 20 },
      { station: 'Vijayawada', estimatedArrival: '01:26', delayMinutes: 20 },
      { station: 'MGR Chennai Central', estimatedArrival: '07:15', delayMinutes: 20 },
    ],
    explanations: [
      {
        type: 'WEATHER',
        title: 'Weather',
        description: 'Storm conditions near Visakhapatnam may add approximately 10 minutes.',
        delayMinutes: 10,
      },
      {
        type: 'TEMPORARY_RESTRICTION',
        title: 'Temporary Speed Restriction',
        description:
          'Temporary speed restriction between Visakhapatnam and Rajahmundry may add approximately 10 minutes.',
        delayMinutes: 10,
      },
    ],
    routeTimeline: [
      { station: 'Shalimar', status: 'COMPLETED' },
      { station: 'Kharagpur', status: 'COMPLETED' },
      { station: 'Balasore', status: 'COMPLETED' },
      { station: 'Cuttack', status: 'COMPLETED' },
      { station: 'Bhubaneswar', status: 'CURRENT' },
      { station: 'Brahmapur', status: 'NEXT', estimatedArrival: '16:38' },
      { station: 'Visakhapatnam', status: 'UPCOMING', estimatedArrival: '20:23' },
      { station: 'Rajahmundry', status: 'UPCOMING', estimatedArrival: '23:23' },
      { station: 'Vijayawada', status: 'UPCOMING', estimatedArrival: '01:26' },
      { station: 'MGR Chennai Central', status: 'DESTINATION', estimatedArrival: '07:15' },
    ],
  },

  '12259': {
    train: {
      trainNumber: '12259',
      trainName: 'Sealdah-New Delhi Duronto Express',
      source: 'Sealdah',
      destination: 'New Delhi',
    },
    currentState: {
      currentStation: 'Pt DD Upadhyaya',
      nextStation: 'Prayagraj',
      progressPercentage: 50,
      currentDelayMinutes: 0,
      lastUpdated: '14:00',
    },
    eta: {
      destination: 'New Delhi',
      scheduledArrival: '21:55',
      estimatedArrival: '21:55',
      remainingMinutes: 475,
    },
    delayBreakdown: {
      baselineRunningMinutes: 463,
      dwellMinutes: 12,
      weatherDelayMinutes: 0,
      incidentDelayMinutes: 0,
      mlCorrectionMinutes: 0,
      totalRemainingMinutes: 475,
    },
    upcomingStations: [
      { station: 'Prayagraj', estimatedArrival: '14:53' },
      { station: 'Kanpur Central', estimatedArrival: '17:08' },
      { station: 'Etawah', estimatedArrival: '18:38' },
      { station: 'New Delhi', estimatedArrival: '21:55' },
    ],
    explanations: [],
    routeTimeline: [
      { station: 'Sealdah', status: 'COMPLETED' },
      { station: 'Asansol', status: 'COMPLETED' },
      { station: 'Dhanbad', status: 'COMPLETED' },
      { station: 'Parasnath', status: 'COMPLETED' },
      { station: 'Gaya', status: 'COMPLETED' },
      { station: 'Pt DD Upadhyaya', status: 'CURRENT' },
      { station: 'Prayagraj', status: 'NEXT', estimatedArrival: '14:53' },
      { station: 'Kanpur Central', status: 'UPCOMING', estimatedArrival: '17:08' },
      { station: 'Etawah', status: 'UPCOMING', estimatedArrival: '18:38' },
      { station: 'New Delhi', status: 'DESTINATION', estimatedArrival: '21:55' },
    ],
  },
};


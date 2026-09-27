package com.predictrack.controller;

import com.predictrack.dto.DashboardResponse;
import com.predictrack.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing the primary PredicTrack dashboard endpoint.
 * Keeps controller logic thin by delegating all orchestration to DashboardService.
 */
@RestController
@RequestMapping("/api/trains")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /api/trains/{trainNumber}/dashboard
     *
     * Returns everything required by the React frontend in a single request:
     * - train information
     * - current train state
     * - destination ETA
     * - current delay
     * - ETA breakdown
     * - upcoming station ETAs
     * - explanations for weather/incident delays
     * - last updated time
     */
    @GetMapping("/{trainNumber}/dashboard")
    public ResponseEntity<DashboardResponse> getTrainDashboard(@PathVariable String trainNumber) {
        DashboardResponse response = dashboardService.getTrainDashboard(trainNumber);
        return ResponseEntity.ok(response);
    }
}

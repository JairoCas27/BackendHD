package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.AccessReportDTO;
import com.urbanpark.parking.dto.OccupationReportDTO;
import com.urbanpark.parking.service.ParkingReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ParkingReportController {

    private final ParkingReportService reportService;

    public ParkingReportController(ParkingReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/access")
    public ResponseEntity<List<AccessReportDTO>> getAccessReport(@RequestParam String tenantId) {
        return ResponseEntity.ok(reportService.getAccessReport(tenantId));
    }

    @GetMapping("/occupation")
    public ResponseEntity<OccupationReportDTO> getOccupationReport(@RequestParam String tenantId) {
        return ResponseEntity.ok(reportService.getOccupationReport(tenantId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccessReportDTO>> getReportByUserOrVehicle(
            @RequestParam String tenantId,
            @RequestParam String criteria) {
        return ResponseEntity.ok(reportService.getReportByUserOrVehicle(tenantId, criteria));
    }
}
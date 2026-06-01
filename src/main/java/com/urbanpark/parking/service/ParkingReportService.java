package com.urbanpark.parking.service;

import com.urbanpark.parking.dto.AccessReportDTO;
import com.urbanpark.parking.dto.OccupationReportDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ParkingReportService {

    public List<AccessReportDTO> getAccessReport(String tenantId) {
        return Arrays.asList(
            new AccessReportDTO("ABC-123", "Juan Pérez", LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1), 120L),
            new AccessReportDTO("XYZ-789", "Ana Gómez", LocalDateTime.now().minusMinutes(45), null, null) // Nulo significa que sigue adentro
        );
    }

    public OccupationReportDTO getOccupationReport(String tenantId) {
        int totalSpacesMock = 100; 
        int occupiedSpacesMock = 42; 
        return new OccupationReportDTO(totalSpacesMock, occupiedSpacesMock);
    }

    public List<AccessReportDTO> getReportByUserOrVehicle(String tenantId, String filter) {
        return Arrays.asList(
            new AccessReportDTO("ABC-123", "Juan Pérez", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(2), 120L)
        );
    }
}


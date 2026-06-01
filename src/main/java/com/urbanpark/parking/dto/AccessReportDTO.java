package com.urbanpark.parking.dto;

import java.time.LocalDateTime;

public class AccessReportDTO {
    private String plate;
    private String username;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long durationInMinutes;

    public AccessReportDTO(String plate, String username, LocalDateTime entryTime, LocalDateTime exitTime, Long durationInMinutes) {
        this.plate = plate;
        this.username = username;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.durationInMinutes = durationInMinutes;
    }

    public String getPlate() { return plate; }
    public String getUsername() { return username; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public Long getDurationInMinutes() { return durationInMinutes; }
}


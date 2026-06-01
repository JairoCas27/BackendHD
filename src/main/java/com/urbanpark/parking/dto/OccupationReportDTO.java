package com.urbanpark.parking.dto;

public class OccupationReportDTO {
    private int totalSpaces;
    private int occupiedSpaces;
    private int availableSpaces;
    private double occupationPercentage;

    public OccupationReportDTO(int totalSpaces, int occupiedSpaces) {
        this.totalSpaces = totalSpaces;
        this.occupiedSpaces = occupiedSpaces;
        this.availableSpaces = totalSpaces - occupiedSpaces;
        this.occupationPercentage = totalSpaces > 0 ? ((double) occupiedSpaces / totalSpaces) * 100 : 0.0;
    }

    // Getters
    public int getTotalSpaces() { return totalSpaces; }
    public int getOccupiedSpaces() { return occupiedSpaces; }
    public int getAvailableSpaces() { return availableSpaces; }
    public double getOccupationPercentage() { return occupationPercentage; }
}


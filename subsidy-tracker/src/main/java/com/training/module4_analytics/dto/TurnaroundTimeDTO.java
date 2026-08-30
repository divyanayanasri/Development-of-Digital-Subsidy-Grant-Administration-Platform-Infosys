package com.training.module4_analytics.dto;

public class TurnaroundTimeDTO {
    public TurnaroundTimeDTO() {}

    private String status;
    private Double averageTurnaroundTimeInHours;
    private Long applicationCount;

    public TurnaroundTimeDTO(String status, Double averageTurnaroundTimeInHours, Long applicationCount) {
        this.status = status;
        this.averageTurnaroundTimeInHours = averageTurnaroundTimeInHours;
        this.applicationCount = applicationCount;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAverageTurnaroundTimeInHours() { return averageTurnaroundTimeInHours; }
    public void setAverageTurnaroundTimeInHours(Double averageTurnaroundTimeInHours) { this.averageTurnaroundTimeInHours = averageTurnaroundTimeInHours; }

    public Long getApplicationCount() { return applicationCount; }
    public void setApplicationCount(Long applicationCount) { this.applicationCount = applicationCount; }

    public static TurnaroundTimeDTOBuilder builder() {
        return new TurnaroundTimeDTOBuilder();
    }

    public static class TurnaroundTimeDTOBuilder {
        private String status;
        private Double averageTurnaroundTimeInHours;
        private Long applicationCount;

        public TurnaroundTimeDTOBuilder status(String status) { this.status = status; return this; }
        public TurnaroundTimeDTOBuilder averageTurnaroundTimeInHours(Double averageTurnaroundTimeInHours) { this.averageTurnaroundTimeInHours = averageTurnaroundTimeInHours; return this; }
        public TurnaroundTimeDTOBuilder applicationCount(Long applicationCount) { this.applicationCount = applicationCount; return this; }

        public TurnaroundTimeDTO build() {
            return new TurnaroundTimeDTO(status, averageTurnaroundTimeInHours, applicationCount);
        }
    }
}

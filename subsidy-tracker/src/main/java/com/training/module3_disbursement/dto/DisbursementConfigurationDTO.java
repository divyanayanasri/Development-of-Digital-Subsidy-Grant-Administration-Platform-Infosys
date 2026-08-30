package com.training.module3_disbursement.dto;

import java.time.LocalDate;
import java.util.List;

public class DisbursementConfigurationDTO {
    private Long applicationId;
    private List<StageConfigDTO> stages;

    public DisbursementConfigurationDTO() {}

    public DisbursementConfigurationDTO(Long applicationId, List<StageConfigDTO> stages) {
        this.applicationId = applicationId;
        this.stages = stages;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public List<StageConfigDTO> getStages() {
        return stages;
    }

    public void setStages(List<StageConfigDTO> stages) {
        this.stages = stages;
    }

    public static class StageConfigDTO {
        private int stageNo;
        private String milestoneName;
        private double percentage;
        private LocalDate dueDate;

        public StageConfigDTO() {}

        public StageConfigDTO(int stageNo, String milestoneName, double percentage, LocalDate dueDate) {
            this.stageNo = stageNo;
            this.milestoneName = milestoneName;
            this.percentage = percentage;
            this.dueDate = dueDate;
        }

        public int getStageNo() {
            return stageNo;
        }

        public void setStageNo(int stageNo) {
            this.stageNo = stageNo;
        }

        public String getMilestoneName() {
            return milestoneName;
        }

        public void setMilestoneName(String milestoneName) {
            this.milestoneName = milestoneName;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }
    }
}

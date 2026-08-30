package com.training.module4_analytics.dto;

public class NonComplianceDTO {
    public NonComplianceDTO() {}

    private Long schemeId;
    private String schemeName;
    private Long nonComplianceCount;

    public NonComplianceDTO(Long schemeId, String schemeName, Long nonComplianceCount) {
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.nonComplianceCount = nonComplianceCount;
    }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }

    public Long getNonComplianceCount() { return nonComplianceCount; }
    public void setNonComplianceCount(Long nonComplianceCount) { this.nonComplianceCount = nonComplianceCount; }

    public static NonComplianceDTOBuilder builder() {
        return new NonComplianceDTOBuilder();
    }

    public static class NonComplianceDTOBuilder {
        private Long schemeId;
        private String schemeName;
        private Long nonComplianceCount;

        public NonComplianceDTOBuilder schemeId(Long schemeId) { this.schemeId = schemeId; return this; }
        public NonComplianceDTOBuilder schemeName(String schemeName) { this.schemeName = schemeName; return this; }
        public NonComplianceDTOBuilder nonComplianceCount(Long nonComplianceCount) { this.nonComplianceCount = nonComplianceCount; return this; }

        public NonComplianceDTO build() {
            return new NonComplianceDTO(schemeId, schemeName, nonComplianceCount);
        }
    }
}

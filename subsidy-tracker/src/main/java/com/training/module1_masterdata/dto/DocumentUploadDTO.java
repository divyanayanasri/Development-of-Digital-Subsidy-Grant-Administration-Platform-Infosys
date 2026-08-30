package com.training.module1_masterdata.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentUploadDTO {
    @NotBlank(message = "Document type is required")
    private String docType;

    @NotBlank(message = "File path is required")
    private String filePath;

    public DocumentUploadDTO() {}

    public DocumentUploadDTO(String docType, String filePath) {
        this.docType = docType;
        this.filePath = filePath;
    }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public static DocumentUploadDTOBuilder builder() {
        return new DocumentUploadDTOBuilder();
    }

    public static class DocumentUploadDTOBuilder {
        private String docType;
        private String filePath;

        public DocumentUploadDTOBuilder docType(String docType) { this.docType = docType; return this; }
        public DocumentUploadDTOBuilder filePath(String filePath) { this.filePath = filePath; return this; }

        public DocumentUploadDTO build() {
            return new DocumentUploadDTO(docType, filePath);
        }
    }
}

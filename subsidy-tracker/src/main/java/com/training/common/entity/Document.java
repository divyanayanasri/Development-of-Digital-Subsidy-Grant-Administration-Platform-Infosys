package com.training.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {
    public Document() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long beneficiaryId;

    @Column(nullable = false)
    private String docType;

    @Column(nullable = false)
    private String filePath;

    private LocalDateTime uploadedAt;

    private boolean verified;

    public Document(Long id, Long beneficiaryId, String docType, String filePath, LocalDateTime uploadedAt, boolean verified) {
        this.id = id;
        this.beneficiaryId = beneficiaryId;
        this.docType = docType;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
        this.verified = verified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public static DocumentBuilder builder() {
        return new DocumentBuilder();
    }

    public static class DocumentBuilder {
        private Long id;
        private Long beneficiaryId;
        private String docType;
        private String filePath;
        private LocalDateTime uploadedAt;
        private boolean verified;

        public DocumentBuilder id(Long id) { this.id = id; return this; }
        public DocumentBuilder beneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; return this; }
        public DocumentBuilder docType(String docType) { this.docType = docType; return this; }
        public DocumentBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public DocumentBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }
        public DocumentBuilder verified(boolean verified) { this.verified = verified; return this; }

        public Document build() {
            return new Document(id, beneficiaryId, docType, filePath, uploadedAt, verified);
        }
    }
}

package com.training.module1_masterdata.service;

import com.training.common.entity.Document;
import com.training.module1_masterdata.dto.DocumentUploadDTO;
import java.util.List;

public interface DocumentService {
    Document uploadDocument(Long beneficiaryId, DocumentUploadDTO dto);
    List<Document> getDocumentsByBeneficiaryId(Long beneficiaryId);
    boolean checkRequiredDocuments(Long beneficiaryId);
    Document verifyDocument(Long documentId);
}

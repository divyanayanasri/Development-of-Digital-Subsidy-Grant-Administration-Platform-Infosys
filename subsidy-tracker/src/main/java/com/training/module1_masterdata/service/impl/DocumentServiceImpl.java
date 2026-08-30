package com.training.module1_masterdata.service.impl;

import com.training.common.entity.Document;
import com.training.common.exception.ResourceNotFoundException;
import com.training.module1_masterdata.dto.DocumentUploadDTO;
import com.training.module1_masterdata.repository.BeneficiaryRepository;
import com.training.module1_masterdata.repository.DocumentRepository;
import com.training.module1_masterdata.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    private static final List<String> REQUIRED_DOC_TYPES = Arrays.asList(
            "Aadhaar Card",
            "PAN Card",
            "Electricity Bill",
            "Property Ownership Proof",
            "Bank Account Details",
            "Passport-size Photograph"
    );

    @Override
    public Document uploadDocument(Long beneficiaryId, DocumentUploadDTO dto) {
        if (!beneficiaryRepository.existsById(beneficiaryId)) {
            throw new ResourceNotFoundException("Beneficiary not found with ID: " + beneficiaryId);
        }

        Document document = Document.builder()
                .beneficiaryId(beneficiaryId)
                .docType(dto.getDocType())
                .filePath(dto.getFilePath())
                .uploadedAt(LocalDateTime.now())
                .verified(false)
                .build();
        
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> getDocumentsByBeneficiaryId(Long beneficiaryId) {
        if (!beneficiaryRepository.existsById(beneficiaryId)) {
            throw new ResourceNotFoundException("Beneficiary not found with ID: " + beneficiaryId);
        }
        return documentRepository.findByBeneficiaryId(beneficiaryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkRequiredDocuments(Long beneficiaryId) {
        List<Document> docs = getDocumentsByBeneficiaryId(beneficiaryId);
        Set<String> uploadedTypes = docs.stream()
                .map(Document::getDocType)
                .collect(Collectors.toSet());
        
        return uploadedTypes.containsAll(REQUIRED_DOC_TYPES);
    }

    @Override
    public Document verifyDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));
        document.setVerified(true);
        return documentRepository.save(document);
    }
}

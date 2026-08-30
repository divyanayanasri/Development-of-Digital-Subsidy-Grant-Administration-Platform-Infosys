package com.training.module1_masterdata.controller;

import com.training.common.entity.Document;
import com.training.module1_masterdata.dto.DocumentUploadDTO;
import com.training.module1_masterdata.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries/{id}/documents")
@Validated
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<Document> uploadDocument(
            @PathVariable("id") Long id,
            @Valid @RequestBody DocumentUploadDTO dto) {
        Document document = documentService.uploadDocument(id, dto);
        return new ResponseEntity<>(document, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<List<Document>> getDocumentsByBeneficiaryId(@PathVariable("id") Long id) {
        List<Document> documents = documentService.getDocumentsByBeneficiaryId(id);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/check")
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Boolean> checkRequiredDocuments(@PathVariable("id") Long id) {
        boolean allUploaded = documentService.checkRequiredDocuments(id);
        return ResponseEntity.ok(allUploaded);
    }

    @PutMapping("/{docId}/verify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER')")
    public ResponseEntity<Document> verifyDocument(
            @PathVariable("id") Long beneficiaryId,
            @PathVariable("docId") Long docId) {
        Document doc = documentService.verifyDocument(docId);
        return ResponseEntity.ok(doc);
    }
}

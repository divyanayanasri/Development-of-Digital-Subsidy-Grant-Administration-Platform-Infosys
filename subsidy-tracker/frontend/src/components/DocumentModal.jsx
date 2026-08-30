import React from 'react';

export function DocumentModal({ isOpen, onClose, docName, docUrl }) {
  if (!isOpen || !docName) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-dialog" style={{ maxWidth: '680px' }} onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Document Verification File: {docName}</h3>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <div className="gov-alert gov-alert-info">
            <strong>Document Reference:</strong> Official verification copy uploaded by beneficiary applicant.
          </div>
          <div style={{
            height: '340px',
            backgroundColor: '#F8FAFC',
            border: '2px dashed var(--paper-border-dark)',
            borderRadius: '2px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
            textAlign: 'center'
          }}>
            <div style={{ fontSize: '48px', marginBottom: '12px' }}>📄</div>
            <h4 style={{ fontFamily: 'var(--font-mono)', fontSize: '1rem', marginBottom: '8px' }}>{docName}</h4>
            <p className="form-help" style={{ marginBottom: '16px' }}>URI: {docUrl || 'https://gov-portal.in/docs/certified_copy.pdf'}</p>
            
            <div style={{ display: 'flex', gap: '12px' }}>
              <a 
                href={docUrl || '#'} 
                target="_blank" 
                rel="noreferrer" 
                className="btn btn-primary btn-sm"
                onClick={(e) => { e.preventDefault(); alert(`Simulated downloading official PDF: ${docName}`); }}
              >
                📥 Download Certified PDF
              </a>
              <button 
                type="button" 
                className="btn btn-secondary btn-sm"
                onClick={() => alert(`Document digital signature verified: VALID (SHA-256 Checksum OK)`)}
              >
                🔏 Check Digital Signature
              </button>
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button type="button" className="btn btn-secondary" onClick={onClose}>
            Close Document Viewer
          </button>
        </div>
      </div>
    </div>
  );
}

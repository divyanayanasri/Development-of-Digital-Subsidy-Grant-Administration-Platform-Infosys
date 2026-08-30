import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../../api/client';
import { ScoreStamp } from '../../components/ScoreStamp';
import { StatusPill } from '../../components/StatusPill';
import { TimelineView } from '../../components/TimelineView';
import { DocumentModal } from '../../components/DocumentModal';
import { DisbursementPanel } from '../../components/DisbursementPanel';

export function ApplicationDetailPage() {
  const { id } = useParams();
  const [app, setApp] = useState(null);
  const [history, setHistory] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Document modal viewer state
  const [activeDoc, setActiveDoc] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError('');
    Promise.all([
      api.getApplicationDetail(id),
      api.getApplicationHistory(id).catch(() => [])
    ]).then(([appRes, histRes]) => {
      setApp(appRes);
      setHistory(Array.isArray(histRes) ? histRes : []);
      if (appRes?.beneficiaryId) {
        api.getBeneficiaryDocuments(appRes.beneficiaryId)
          .then(docs => setDocuments(Array.isArray(docs) ? docs : []))
          .catch(() => setDocuments([]));
      }
      setLoading(false);
    }).catch(err => {
      setError(err.message || 'Case file record not found.');
      setLoading(false);
    });
  }, [id]);

  if (loading) {
    return <div className="ledger-card" style={{ padding: '40px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>Loading case file details...</div>;
  }

  if (error || !app) {
    return <div className="gov-alert gov-alert-error">{error || 'Case file not found.'}</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
        <Link to="/beneficiary/applications" className="btn btn-secondary btn-sm">
          ← Back to My Applications
        </Link>
        <span className="gov-badge">OFFICIAL READ-ONLY CASE FILE AUDIT</span>
      </div>

      {/* Header Ledger Box */}
      <div className="ledger-card" style={{ padding: '24px' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'flex-start', justifyContent: 'space-between', gap: '20px', borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '20px', marginBottom: '20px' }}>
          <div>
            <span className="font-mono" style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>APPLICATION FILE ID: #{app.id}</span>
            <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.75rem', color: 'var(--navy-dark)', margin: '4px 0 8px 0' }}>
              Application #{app.id}
            </h2>
            <div style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--brass-hover)' }}>
              Scheme ID: {app.schemeId}
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
            <ScoreStamp score={app.eligibilityScore} routeType={app.routeType} size="md" />
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>CURRENT STAGE</div>
              <StatusPill status={app.status} />
            </div>
          </div>
        </div>

        {/* Data Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '24px' }}>
          <div style={{ backgroundColor: '#F8FAFC', padding: '12px 16px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
            <span className="form-label" style={{ marginBottom: '2px', color: 'var(--text-muted)' }}>Beneficiary ID</span>
            <div className="font-mono" style={{ fontWeight: 600, fontSize: '0.95rem' }}>#{app.beneficiaryId}</div>
          </div>

          <div style={{ backgroundColor: '#F8FAFC', padding: '12px 16px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
            <span className="form-label" style={{ marginBottom: '2px', color: 'var(--text-muted)' }}>Eligibility Score</span>
            <div className="font-mono" style={{ fontWeight: 600, fontSize: '0.95rem', color: '#166534' }}>
              {app.eligibilityScore} / 100
            </div>
          </div>

          <div style={{ backgroundColor: '#F8FAFC', padding: '12px 16px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
            <span className="form-label" style={{ marginBottom: '2px', color: 'var(--text-muted)' }}>Workflow Route</span>
            <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{app.routeType}</div>
          </div>

          <div style={{ backgroundColor: '#F8FAFC', padding: '12px 16px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
            <span className="form-label" style={{ marginBottom: '2px', color: 'var(--text-muted)' }}>Submission Date</span>
            <div className="font-mono" style={{ fontWeight: 600, fontSize: '0.95rem' }}>
              {app.submittedAt ? new Date(app.submittedAt).toLocaleDateString('en-IN') : 'Recently Submitted'}
            </div>
          </div>
        </div>

        {/* Attached Verification Documents */}
        <div style={{ marginBottom: '28px' }}>
          <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '12px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
            Attached Beneficiary Documents ({documents.length} Files)
          </div>

          {documents.length === 0 ? (
            <div style={{ fontSize: '0.84rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
              No document files uploaded.
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '12px' }}>
              {documents.map((doc, idx) => (
                <div 
                  key={idx} 
                  style={{
                    backgroundColor: '#FFFFFF',
                    border: '1px solid var(--paper-border-dark)',
                    borderRadius: '2px',
                    padding: '12px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, fontSize: '0.84rem' }}>📄 {doc.docType || 'Verification Doc'}</div>
                    <div className="font-mono" style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{doc.filePath || 'VERIFIED'}</div>
                  </div>
                  <button 
                    type="button" 
                    className="btn btn-secondary btn-sm"
                    onClick={() => setActiveDoc({ name: doc.docType, url: doc.filePath })}
                  >
                    View File
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Verification Audit Trail Timeline */}
        <div style={{ marginBottom: '28px' }}>
          <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '12px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
            Official Verification Audit History Ledger
          </div>
          <TimelineView history={history} />
        </div>
      </div>

      {/* Milestone 3: Staged Disbursement Panel when APPROVED */}
      {app.status === 'APPROVED' && (
        <DisbursementPanel applicationId={app.id} />
      )}

      {/* Document Viewer Modal */}
      {activeDoc && (
        <DocumentModal 
          isOpen={!!activeDoc} 
          onClose={() => setActiveDoc(null)} 
          docName={activeDoc.name} 
          docUrl={activeDoc.url} 
        />
      )}
    </div>
  );
}

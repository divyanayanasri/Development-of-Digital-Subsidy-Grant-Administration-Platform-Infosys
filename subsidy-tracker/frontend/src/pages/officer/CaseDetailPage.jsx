import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../api/client';
import { getValidNextStatuses, ACTION_LABELS, ACTION_VARIANTS } from '../../utils/transitions';
import { ScoreStamp } from '../../components/ScoreStamp';
import { StatusPill } from '../../components/StatusPill';
import { TimelineView } from '../../components/TimelineView';
import { VerificationModal } from '../../components/VerificationModal';
import { DocumentModal } from '../../components/DocumentModal';
import { DisbursementPanel } from '../../components/DisbursementPanel';

export function CaseDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const role = user?.role || 'FIELD_OFFICER';

  const [app, setApp] = useState(null);
  const [history, setHistory] = useState([]);
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Action Verification Modal state
  const [targetActionStatus, setTargetActionStatus] = useState(null);

  // Document Viewer modal state
  const [activeDoc, setActiveDoc] = useState(null);

  const loadCase = () => {
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
          .then(d => setDocs(Array.isArray(d) ? d : []))
          .catch(() => setDocs([]));
      }
      setLoading(false);
    }).catch(err => {
      setError(err.message || 'Case file record not found.');
      setLoading(false);
    });
  };

  useEffect(() => {
    loadCase();
  }, [id]);

  const handleActionConfirm = async (actionStatus, remarks) => {
    if (!app) return;
    try {
      const payload = {
        officerId: user?.id || user?.userId || 1,
        role: user?.role || role,
        targetStatus: actionStatus,
        decision: actionStatus,
        remarks: remarks || `Transitioned to ${actionStatus}`
      };

      const updated = await api.transitionApplication(app.id, payload);
      setTargetActionStatus(null);
      setApp(updated);
      alert(`Action recorded successfully: ${ACTION_LABELS[actionStatus] || actionStatus}`);
      loadCase();
    } catch (err) {
      alert(err.message || 'Action verification failed.');
    }
  };

  if (loading) {
    return <div className="ledger-card" style={{ padding: '40px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>Loading case file details...</div>;
  }

  if (error || !app) {
    return <div className="gov-alert gov-alert-error">{error || 'Case file not found.'}</div>;
  }

  const validNextActions = getValidNextStatuses(app.routeType || 'FAST_TRACK', app.status, role);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
        <Link to="/officer/queue" className="btn btn-secondary btn-sm">
          ← Back to Verification Queue
        </Link>
        <span className="gov-badge">SHAREABLE CASE FILE DIRECTORY</span>
      </div>

      <div className="ledger-card" style={{ padding: '28px' }}>
        {/* Case File Header */}
        <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'flex-start', justifyContent: 'space-between', gap: '20px', borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '20px', marginBottom: '24px' }}>
          <div>
            <span className="font-mono" style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>APPLICATION ID: #{app.id}</span>
            <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.85rem', color: 'var(--navy-dark)', margin: '4px 0 8px 0' }}>
              Case File: Application #{app.id}
            </h2>
            <div style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--brass-hover)' }}>
              Scheme ID: #{app.schemeId} • Beneficiary ID: #{app.beneficiaryId}
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

        {/* Action Panel for Officer */}
        <div style={{ backgroundColor: '#F1F5F9', border: '2px solid var(--navy-border)', borderRadius: '2px', padding: '20px', marginBottom: '28px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
            <span className="form-label" style={{ margin: 0, color: 'var(--navy-dark)', fontSize: '0.95rem' }}>
              ⚖️ Officer Action Control Panel ({role})
            </span>
            <span className="user-role-badge">ROUTE: {app.routeType}</span>
          </div>

          {validNextActions.length === 0 ? (
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
              No pending transition actions available for your role ({role}) at current stage ({app.status}).
            </div>
          ) : (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px' }}>
              {validNextActions.map(actionStatus => {
                const label = ACTION_LABELS[actionStatus] || actionStatus;
                const btnClass = ACTION_VARIANTS[actionStatus] || 'btn-primary';
                return (
                  <button
                    key={actionStatus}
                    type="button"
                    className={`btn ${btnClass}`}
                    style={{ padding: '10px 20px', fontSize: '0.9rem' }}
                    onClick={() => setTargetActionStatus(actionStatus)}
                  >
                    {label}
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Attached Documents */}
        <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '12px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
          Attached Beneficiary Documents ({docs.length})
        </div>
        {docs.length === 0 ? (
          <div style={{ fontSize: '0.84rem', color: 'var(--text-muted)', fontStyle: 'italic', marginBottom: '28px' }}>No document files attached.</div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '12px', marginBottom: '28px' }}>
            {docs.map((doc, idx) => (
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
                  <div style={{ fontWeight: 600, fontSize: '0.86rem' }}>📄 {doc.docType}</div>
                  <div className="font-mono" style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{doc.filePath || 'VERIFIED'}</div>
                </div>
                <button 
                  type="button" 
                  className="btn btn-secondary btn-sm"
                  onClick={() => setActiveDoc({ name: doc.docType, url: doc.filePath })}
                >
                  Inspect File
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Milestone 3: Staged Disbursement Panel when APPROVED */}
        {app.status === 'APPROVED' && (
          <div style={{ marginBottom: '28px' }}>
            <DisbursementPanel applicationId={app.id} />
          </div>
        )}

        {/* Verification Audit Trail Ledger */}
        <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '12px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
          Complete Verification Audit Trail Ledger
        </div>
        <TimelineView history={history} />
      </div>

      {/* Verification Confirmation Modal */}
      {targetActionStatus && (
        <VerificationModal 
          isOpen={!!targetActionStatus}
          onClose={() => setTargetActionStatus(null)}
          onConfirm={handleActionConfirm}
          targetStatus={targetActionStatus}
          caseNumber={`App #${app.id}`}
        />
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

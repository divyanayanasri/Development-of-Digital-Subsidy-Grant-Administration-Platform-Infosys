import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../api/client';
import { getOfficerQueueTabs, getValidNextStatuses, ACTION_LABELS, ACTION_VARIANTS } from '../../utils/transitions';
import { ScoreStamp } from '../../components/ScoreStamp';
import { StatusPill } from '../../components/StatusPill';
import { TimelineView } from '../../components/TimelineView';
import { VerificationModal } from '../../components/VerificationModal';
import { DocumentModal } from '../../components/DocumentModal';
import { DisbursementPanel } from '../../components/DisbursementPanel';

export function OfficerQueuePage() {
  const { user } = useAuth();
  const role = user?.role || 'FIELD_OFFICER';
  
  const tabs = getOfficerQueueTabs(role);
  const [activeTab, setActiveTab] = useState(tabs[0]?.id || 'SUBMITTED');
  
  const [applications, setApplications] = useState([]);
  const [selectedCase, setSelectedCase] = useState(null);
  const [caseHistory, setCaseHistory] = useState([]);
  const [caseDocs, setCaseDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Action Verification Modal state
  const [targetActionStatus, setTargetActionStatus] = useState(null);
  
  // Document Viewer modal state
  const [activeDoc, setActiveDoc] = useState(null);

  const fetchQueue = (status) => {
    setLoading(true);
    setError('');
    
    // Default fallback status if tab is ALL
    const targetStatus = status === 'ALL' || status === 'FINANCE_QUEUE' ? 'SUBMITTED' : status;

    api.getApplicationsQueue(targetStatus)
      .then(res => {
        const list = Array.isArray(res) ? res : [];
        setApplications(list);
        setLoading(false);
        if (list.length > 0 && (!selectedCase || !list.find(c => c.id === selectedCase.id))) {
          loadCaseDetails(list[0]);
        } else if (list.length === 0) {
          setSelectedCase(null);
        }
      })
      .catch(err => {
        setError(err.message || 'Failed to load officer verification queue.');
        setApplications([]);
        setSelectedCase(null);
        setLoading(false);
      });
  };

  const loadCaseDetails = (appItem) => {
    setSelectedCase(appItem);
    if (!appItem) return;
    
    Promise.all([
      api.getApplicationHistory(appItem.id).catch(() => []),
      appItem.beneficiaryId ? api.getBeneficiaryDocuments(appItem.beneficiaryId).catch(() => []) : Promise.resolve([])
    ]).then(([hist, docs]) => {
      setCaseHistory(Array.isArray(hist) ? hist : []);
      setCaseDocs(Array.isArray(docs) ? docs : []);
    });
  };

  useEffect(() => {
    fetchQueue(activeTab);
  }, [activeTab, role]);

  const handleActionConfirm = async (actionStatus, remarks) => {
    if (!selectedCase) return;
    try {
      const payload = {
        officerId: user?.id || user?.userId || 1,
        role: user?.role || role,
        targetStatus: actionStatus,
        decision: actionStatus,
        remarks: remarks || `Status transitioned to ${actionStatus} by ${user?.name || role}`
      };

      const updated = await api.transitionApplication(selectedCase.id, payload);
      setTargetActionStatus(null);
      alert(`Action successfully recorded: ${ACTION_LABELS[actionStatus] || actionStatus}`);
      fetchQueue(activeTab);
    } catch (err) {
      alert(err.message || 'Action verification failed.');
    }
  };

  const validNextActions = selectedCase 
    ? getValidNextStatuses(selectedCase.routeType || 'FAST_TRACK', selectedCase.status, role)
    : [];

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
        <div>
          <span className="gov-badge">OFFICER VERIFICATION WORKSTATION</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            Verification Queue Register
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Role: <strong>{role}</strong> • User: <strong>{user?.name || 'Officer'}</strong>
          </p>
        </div>

        <button type="button" className="btn btn-secondary btn-sm" onClick={() => fetchQueue(activeTab)}>
          🔄 Refresh Queue
        </button>
      </div>

      {/* Role Queue Tabs */}
      <div className="tabs-nav">
        {tabs.map(t => (
          <button
            key={t.id}
            type="button"
            className={`tab-btn ${activeTab === t.id ? 'active' : ''}`}
            onClick={() => setActiveTab(t.id)}
          >
            {t.label}
          </button>
        ))}
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      {/* Split Workstation Layout: Left Register Table, Right Case Inspection Panel */}
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(380px, 1fr) minmax(460px, 1.3fr)', gap: '20px' }}>
        
        {/* Left Column: Register Table */}
        <div className="ledger-card" style={{ marginBottom: 0 }}>
          <div className="ledger-header">
            <div className="ledger-title">
              <h2>Queue Applications ({applications.length} Files)</h2>
            </div>
          </div>

          <div className="table-responsive" style={{ maxHeight: '680px', overflowY: 'auto' }}>
            <table className="gov-table">
              <thead>
                <tr>
                  <th>App ID</th>
                  <th>Scheme ID</th>
                  <th>Status</th>
                  <th>Route</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '24px', fontFamily: 'var(--font-mono)' }}>
                      Fetching queue register entries...
                    </td>
                  </tr>
                ) : applications.length === 0 ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                      No applications pending in status stage: {activeTab}
                    </td>
                  </tr>
                ) : (
                  applications.map(app => {
                    const isSelected = selectedCase?.id === app.id;
                    return (
                      <tr 
                        key={app.id} 
                        style={{ cursor: 'pointer', backgroundColor: isSelected ? '#EFF6FF' : undefined }}
                        onClick={() => loadCaseDetails(app)}
                      >
                        <td className="font-mono" style={{ fontWeight: isSelected ? 700 : 500, color: 'var(--navy-dark)' }}>
                          #{app.id}
                        </td>
                        <td>
                          <div style={{ fontWeight: 600, fontSize: '0.84rem' }}>Scheme #{app.schemeId}</div>
                          <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>Ben #{app.beneficiaryId}</div>
                        </td>
                        <td>
                          <StatusPill status={app.status} />
                        </td>
                        <td>
                          <span className={`route-badge route-${app.routeType}`}>
                            {app.routeType === 'FAST_TRACK' ? '⚡ FAST' : '⚠️ ESC'}
                          </span>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Right Column: Interactive Officer Inspection & Action Panel */}
        <div className="ledger-card" style={{ marginBottom: 0, padding: '24px' }}>
          {!selectedCase ? (
            <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--text-muted)' }}>
              Select an application file from the queue table to inspect details.
            </div>
          ) : (
            <div>
              {/* Inspection Header */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '16px', marginBottom: '20px' }}>
                <div>
                  <span className="font-mono" style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>CASE FILE AUDIT</span>
                  <h3 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.4rem', color: 'var(--navy-dark)', margin: '2px 0 6px 0' }}>
                    Application #{selectedCase.id}
                  </h3>
                  <div style={{ fontSize: '0.86rem', color: 'var(--brass-hover)', fontWeight: 600 }}>
                    Scheme ID: #{selectedCase.schemeId} • Beneficiary ID: #{selectedCase.beneficiaryId}
                  </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '8px' }}>
                  <ScoreStamp score={selectedCase.eligibilityScore} routeType={selectedCase.routeType} size="sm" />
                  <Link to={`/case/${selectedCase.id}`} className="btn btn-secondary btn-sm" target="_blank">
                    🔗 Full Page Link ↗
                  </Link>
                </div>
              </div>

              {/* Action Control Panel */}
              <div style={{ backgroundColor: '#F1F5F9', border: '1px solid var(--paper-border-dark)', borderRadius: '2px', padding: '16px', marginBottom: '20px' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '10px' }}>
                  <span className="form-label" style={{ margin: 0, color: 'var(--navy-dark)' }}>
                    ⚡ Officer Action Control ({role})
                  </span>
                  <StatusPill status={selectedCase.status} />
                </div>

                {validNextActions.length === 0 ? (
                  <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                    No pending actions required by {role} for current stage ({selectedCase.status}).
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                    {validNextActions.map(actionStatus => {
                      const label = ACTION_LABELS[actionStatus] || actionStatus;
                      const btnClass = ACTION_VARIANTS[actionStatus] || 'btn-primary';
                      return (
                        <button
                          key={actionStatus}
                          type="button"
                          className={`btn ${btnClass}`}
                          onClick={() => setTargetActionStatus(actionStatus)}
                        >
                          {label}
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>

              {/* Uploaded Beneficiary Documents */}
              <div style={{ marginBottom: '20px' }}>
                <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '8px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border)' }}>
                  Beneficiary Documents ({caseDocs.length})
                </div>
                {caseDocs.length === 0 ? (
                  <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>No documents attached.</div>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                    {caseDocs.map((doc, idx) => (
                      <div 
                        key={idx}
                        style={{
                          padding: '8px 12px',
                          backgroundColor: '#F8FAFC',
                          border: '1px solid var(--paper-border)',
                          borderRadius: '2px',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          fontSize: '0.82rem'
                        }}
                      >
                        <span>📄 <strong>{doc.docType}</strong></span>
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
              </div>

              {/* Staged Disbursement Panel if APPROVED */}
              {selectedCase.status === 'APPROVED' && (
                <div style={{ marginBottom: '20px' }}>
                  <DisbursementPanel applicationId={selectedCase.id} />
                </div>
              )}

              {/* Verification Audit Trail */}
              <div>
                <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '8px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border)' }}>
                  Verification History Ledger
                </div>
                <TimelineView history={caseHistory} />
              </div>
            </div>
          )}
        </div>

      </div>

      {/* Verification Confirmation Modal */}
      {targetActionStatus && selectedCase && (
        <VerificationModal 
          isOpen={!!targetActionStatus}
          onClose={() => setTargetActionStatus(null)}
          onConfirm={handleActionConfirm}
          targetStatus={targetActionStatus}
          caseNumber={`App #${selectedCase.id}`}
        />
      )}

      {/* Document Inspection Modal */}
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

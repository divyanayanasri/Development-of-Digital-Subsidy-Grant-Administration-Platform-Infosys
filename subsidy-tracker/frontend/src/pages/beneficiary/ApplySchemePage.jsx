import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../api/client';
import { ScoreStamp } from '../../components/ScoreStamp';

export function ApplySchemePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [schemes, setSchemes] = useState([]);
  const [selectedScheme, setSelectedScheme] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  
  // Submission Result Modal state
  const [submittedResult, setSubmittedResult] = useState(null);
  const [docInputs, setDocInputs] = useState({
    incomeCert: '',
    landCert: ''
  });

  const benId = user?.beneficiaryId || user?.userId || user?.id;

  useEffect(() => {
    setLoading(true);
    api.getSchemes()
      .then(res => {
        setSchemes(Array.isArray(res) ? res : []);
        setLoading(false);
      })
      .catch(err => {
        setError('Failed to load active government schemes from backend.');
        setLoading(false);
      });
  }, []);

  const handleSelectScheme = (scheme) => {
    setSelectedScheme(scheme);
    setError('');
  };

  const handleSubmitApplication = async (e) => {
    e.preventDefault();
    if (!selectedScheme || !benId) return;

    setError('');
    setSubmitting(true);

    try {
      // Optional: If user uploaded documents, send to beneficiary documents endpoint
      if (docInputs.incomeCert) {
        await api.uploadBeneficiaryDocument(benId, { docType: 'INCOME_CERTIFICATE', filePath: docInputs.incomeCert }).catch(() => {});
      }
      if (docInputs.landCert) {
        await api.uploadBeneficiaryDocument(benId, { docType: 'LAND_CERTIFICATE', filePath: docInputs.landCert }).catch(() => {});
      }

      // Submit application: { beneficiaryId, schemeId } ONLY
      const result = await api.submitApplication(benId, selectedScheme.id);
      setSubmitting(false);
      setSubmittedResult(result);
    } catch (err) {
      setSubmitting(false);
      setError(err.message || 'Failed to submit scheme application.');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <span className="gov-badge">DIRECT BENEFIT TRANSFER SCHEMES</span>
        <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
          Apply for Government Grant / Subsidy Scheme
        </h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
          Browse eligible government subsidy schemes, upload verification documents, and submit your application.
        </p>
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      {/* Scheme Selection Grid */}
      {!selectedScheme ? (
        <div>
          <h3 style={{ fontSize: '1.15rem', marginBottom: '16px', color: 'var(--navy-dark)' }}>
            Available Active Schemes ({schemes.length})
          </h3>

          {loading ? (
            <div className="ledger-card" style={{ padding: '32px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
              Loading active schemes from backend...
            </div>
          ) : schemes.length === 0 ? (
            <div className="ledger-card" style={{ padding: '36px', textAlign: 'center', color: 'var(--text-muted)' }}>
              No active schemes currently available.
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px' }}>
              {schemes.map(scheme => (
                <div key={scheme.id} className="ledger-card" style={{ padding: '20px', display: 'flex', flexDirection: 'column', marginBottom: 0 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '10px' }}>
                    <span className="gov-badge">{scheme.categoryAllowed || 'WELFARE'}</span>
                    <span className="font-mono" style={{ fontSize: '0.74rem', color: '#166534', backgroundColor: '#DCFCE7', padding: '1px 6px', borderRadius: '2px', fontWeight: 600 }}>
                      {scheme.status || 'ACTIVE'}
                    </span>
                  </div>

                  <h4 style={{ fontSize: '1.15rem', marginBottom: '10px', color: 'var(--navy-dark)' }}>
                    {scheme.name}
                  </h4>

                  <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)', marginBottom: '16px', lineHeight: '1.5', flex: 1 }}>
                    {scheme.description || 'Government direct grant assistance program.'}
                  </p>

                  <div style={{ fontSize: '0.84rem', color: 'var(--text-secondary)', marginBottom: '16px', backgroundColor: '#F8FAFC', padding: '12px', border: '1px solid var(--paper-border-dark)', borderRadius: '2px' }}>
                    <div>💰 <strong>Grant Range:</strong> <span className="font-mono">₹{(scheme.grantAmountMin || 0).toLocaleString('en-IN')} – ₹{(scheme.grantAmountMax || 0).toLocaleString('en-IN')}</span></div>
                    <div>📊 <strong>Income Limit:</strong> <span className="font-mono">Up to ₹{(scheme.maxIncome || 0).toLocaleString('en-IN')}/yr</span></div>
                    <div>🌱 <strong>Max Land Size:</strong> <span className="font-mono">Up to {scheme.minLandSize || 0} Acres</span></div>
                  </div>

                  <button 
                    type="button" 
                    className="btn btn-primary" 
                    style={{ marginTop: 'auto', width: '100%' }}
                    onClick={() => handleSelectScheme(scheme)}
                  >
                    Select & Proceed to Apply →
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      ) : (
        /* Application Submission Confirmation Form */
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' }}>
            <button type="button" className="btn btn-secondary btn-sm" onClick={() => setSelectedScheme(null)}>
              ← Back to Scheme Selection
            </button>
            <span className="gov-badge">SELECTED SCHEME: {selectedScheme.name}</span>
          </div>

          <div className="ledger-card" style={{ padding: '28px', maxWidth: '720px' }}>
            <div style={{ borderBottom: '2px solid var(--brass-accent)', paddingBottom: '12px', marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.25rem', color: 'var(--navy-dark)' }}>
                Application Submission Declaration
              </h3>
              <p style={{ fontSize: '0.84rem', color: 'var(--text-secondary)' }}>
                Target Scheme: <strong>{selectedScheme.name}</strong> • Beneficiary ID: <strong className="font-mono">{benId}</strong>
              </p>
            </div>

            <form onSubmit={handleSubmitApplication}>
              <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '12px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
                1. Optional Verification Document Attachments
              </div>

              <div className="form-group">
                <label className="form-label">Income Certificate File Path / URL</label>
                <input 
                  type="text" 
                  className="form-control font-mono"
                  placeholder="e.g. /docs/income_certificate_2026.pdf"
                  value={docInputs.incomeCert}
                  onChange={(e) => setDocInputs(prev => ({ ...prev, incomeCert: e.target.value }))}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Land Ownership Certificate File Path / URL</label>
                <input 
                  type="text" 
                  className="form-control font-mono"
                  placeholder="e.g. /docs/land_record_khasra.pdf"
                  value={docInputs.landCert}
                  onChange={(e) => setDocInputs(prev => ({ ...prev, landCert: e.target.value }))}
                />
              </div>

              <div className="gov-alert gov-alert-info" style={{ marginTop: '16px' }}>
                <div>
                  ℹ️ <strong>Backend Eligibility Evaluation:</strong> Your stored socio-economic profile (annual income, land size, category) will be evaluated automatically by the server algorithm to compute your score and routing assignment.
                </div>
              </div>

              <button type="submit" className="btn btn-brass" style={{ width: '100%', marginTop: '20px', padding: '12px' }} disabled={submitting}>
                {submitting ? 'Submitting Application & Processing Score...' : 'Submit Application to Government Ledger →'}
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Immediate Returned Score & Routing Modal */}
      {submittedResult && (
        <div className="modal-overlay">
          <div className="modal-dialog" style={{ maxWidth: '580px', textAlign: 'center' }}>
            <div className="modal-header">
              <h3>Application Logged into Government Register</h3>
            </div>
            <div className="modal-body" style={{ padding: '28px' }}>
              <div style={{ marginBottom: '16px' }}>
                <span className="gov-badge" style={{ fontSize: '0.85rem' }}>APPLICATION ID: #{submittedResult.id}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'center', margin: '20px 0' }}>
                <ScoreStamp score={submittedResult.eligibilityScore} routeType={submittedResult.routeType} size="md" />
              </div>

              <h3 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.4rem', color: 'var(--navy-dark)', marginBottom: '8px' }}>
                Server Score Generated: {submittedResult.eligibilityScore} / 100
              </h3>

              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '24px' }}>
                Application successfully created in official ledger. Workflow Route: <strong>{submittedResult.routeType}</strong>. Initial Status: <strong>{submittedResult.status}</strong>.
              </p>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                <button 
                  type="button" 
                  className="btn btn-primary" 
                  onClick={() => navigate(`/beneficiary/application/${submittedResult.id}`)}
                >
                  View Case File Detail →
                </button>
                <button 
                  type="button" 
                  className="btn btn-secondary" 
                  onClick={() => navigate('/beneficiary/applications')}
                >
                  Go to My Applications Register
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

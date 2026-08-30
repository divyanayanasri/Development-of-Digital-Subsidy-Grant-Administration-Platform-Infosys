import React, { useState, useEffect } from 'react';
import { api } from '../../api/client';
import { Link } from 'react-router-dom';

export function AdminCompliancePage() {
  const [flags, setFlags] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    api.getNonCompliantDisbursements()
      .then(res => {
        setFlags(Array.isArray(res) ? res : []);
        setLoading(false);
      })
      .catch(err => {
        setError('Failed to fetch non-compliance reports.');
        setLoading(false);
      });
  }, []);

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <span className="gov-badge">DIRECTORATE COMPLIANCE MONITORS</span>
        <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
          Disbursement Non-Compliance & Audit Flags
        </h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
          Real-time compliance monitoring register tracking flagged milestone disbursements and audit anomalies across state schemes.
        </p>
      </div>

      {error && <div className="gov-alert gov-alert-error">{error}</div>}

      {loading ? (
        <div className="ledger-card" style={{ padding: '32px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
          Loading compliance audit register...
        </div>
      ) : (
        <div className="ledger-card">
          <div className="ledger-header">
            <div className="ledger-title">
              <h2>Flagged Non-Compliant Disbursement Stages ({flags.length} Incidents)</h2>
            </div>
          </div>

          <div className="table-responsive">
            <table className="gov-table">
              <thead>
                <tr>
                  <th>Flag ID</th>
                  <th>Application File ID</th>
                  <th>Disbursement Stage ID</th>
                  <th>Compliance Flag Type</th>
                  <th>Date Raised</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Case File Link</th>
                </tr>
              </thead>
              <tbody>
                {flags.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                      ✓ No non-compliant disbursement flags detected. All active stages meet compliance standards.
                    </td>
                  </tr>
                ) : (
                  flags.map(flag => (
                    <tr key={flag.id}>
                      <td className="font-mono" style={{ fontWeight: 700, color: 'var(--navy-dark)' }}>
                        #{flag.id}
                      </td>
                      <td className="font-mono">
                        App #{flag.applicationId}
                      </td>
                      <td className="font-mono">
                        Stage #{flag.stageId}
                      </td>
                      <td>
                        <span className="gov-badge" style={{ backgroundColor: '#FEE2E2', color: '#991B1B', borderColor: '#FCA5A5' }}>
                          ⚠️ {flag.flagType || 'VERIFICATION_ANOMALY'}
                        </span>
                      </td>
                      <td className="font-mono" style={{ fontSize: '0.82rem' }}>
                        {flag.raisedAt ? new Date(flag.raisedAt).toLocaleString('en-IN') : 'N/A'}
                      </td>
                      <td>
                        <span className={`status-pill status-${flag.resolved ? 'APPROVED' : 'REJECTED'}`}>
                          {flag.resolved ? 'RESOLVED' : 'ACTIVE FLAG'}
                        </span>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <Link to={`/case/${flag.applicationId}`} className="btn btn-secondary btn-sm" target="_blank">
                          View Case File ↗
                        </Link>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

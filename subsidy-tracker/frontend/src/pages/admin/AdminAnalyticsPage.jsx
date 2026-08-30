import React, { useState, useEffect } from 'react';
import { api } from '../../api/client';

export function AdminAnalyticsPage() {
  const [fundUtilization, setFundUtilization] = useState([]);
  const [budgetExhaustion, setBudgetExhaustion] = useState([]);
  const [nonCompliance, setNonCompliance] = useState([]);
  const [turnaroundTimes, setTurnaroundTimes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    Promise.all([
      api.getFundUtilization().catch(() => []),
      api.getBudgetExhaustion().catch(() => []),
      api.getAnalyticsNonCompliance().catch(() => []),
      api.getTurnaroundTimes().catch(() => [])
    ]).then(([fundRes, budgetRes, nonCompRes, turnaroundRes]) => {
      setFundUtilization(Array.isArray(fundRes) ? fundRes : []);
      setBudgetExhaustion(Array.isArray(budgetRes) ? budgetRes : []);
      setNonCompliance(Array.isArray(nonCompRes) ? nonCompRes : []);
      setTurnaroundTimes(Array.isArray(turnaroundRes) ? turnaroundRes : []);
      setLoading(false);
    }).catch(err => {
      setError('Failed to fetch analytics datasets.');
      setLoading(false);
    });
  }, []);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <span className="gov-badge">DIRECTORATE STATEWIDE ANALYTICS</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            Fund Utilization & Regional Analytics Dashboard
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Real-time analytics on scheme fund distribution, regional budget exhaustion, compliance counts, and verification turnaround times.
          </p>
        </div>

        {/* Export Buttons */}
        <div style={{ display: 'flex', gap: '12px' }}>
          <a 
            href={api.getReportExportUrl('pdf')} 
            target="_blank" 
            rel="noopener noreferrer" 
            className="btn btn-secondary"
            style={{ fontWeight: 600 }}
          >
            📄 Export PDF Report
          </a>
          <a 
            href={api.getReportExportUrl('excel')} 
            target="_blank" 
            rel="noopener noreferrer" 
            className="btn btn-brass"
            style={{ fontWeight: 600 }}
          >
            📊 Export Excel Report
          </a>
        </div>
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      {loading ? (
        <div className="ledger-card" style={{ padding: '40px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
          Loading state analytics matrices...
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(480px, 1fr))', gap: '24px' }}>
          
          {/* Panel 1: Fund Utilization per Scheme */}
          <div className="ledger-card" style={{ padding: '24px', marginBottom: 0 }}>
            <div style={{ borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '12px', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.2rem', color: 'var(--navy-dark)' }}>
                1. Fund Utilization by Scheme
              </h3>
              <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', margin: 0 }}>
                Comparison between allocated budget and actual funds released to beneficiaries.
              </p>
            </div>

            {fundUtilization.length === 0 ? (
              <div style={{ padding: '20px', textAlign: 'center', color: 'var(--text-muted)' }}>No fund utilization records found.</div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {fundUtilization.map((item, idx) => {
                  const allocated = Number(item.allocatedAmount || 0);
                  const released = Number(item.releasedAmount || 0);
                  const pct = allocated > 0 ? Math.min(100, Math.round((released / allocated) * 100)) : 0;

                  return (
                    <div key={idx} style={{ backgroundColor: '#F8FAFC', padding: '14px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
                        <strong style={{ fontSize: '0.9rem', color: 'var(--navy-dark)' }}>{item.schemeName || `Scheme #${item.schemeId}`}</strong>
                        <span className="font-mono" style={{ fontWeight: 700, color: '#166534' }}>{pct}% Disbursed</span>
                      </div>
                      <div className="progress-bar-bg" style={{ marginBottom: '8px' }}>
                        <div className="progress-bar-fill" style={{ width: `${pct}%` }} />
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                        <span>Allocated: <strong className="font-mono">₹{allocated.toLocaleString('en-IN')}</strong></span>
                        <span>Released: <strong className="font-mono" style={{ color: '#166534' }}>₹{released.toLocaleString('en-IN')}</strong></span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Panel 2: Budget Exhaustion per Region */}
          <div className="ledger-card" style={{ padding: '24px', marginBottom: 0 }}>
            <div style={{ borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '12px', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.2rem', color: 'var(--navy-dark)' }}>
                2. Regional Budget Exhaustion
              </h3>
              <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', margin: 0 }}>
                Budget caps versus committed funds across regional jurisdictions.
              </p>
            </div>

            {budgetExhaustion.length === 0 ? (
              <div style={{ padding: '20px', textAlign: 'center', color: 'var(--text-muted)' }}>No budget exhaustion records found.</div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {budgetExhaustion.map((item, idx) => {
                  const pct = Number(item.exhaustionPercentage || 0);
                  const isHigh = pct >= 80;

                  return (
                    <div key={idx} style={{ backgroundColor: '#F8FAFC', padding: '14px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
                        <strong style={{ fontSize: '0.9rem', color: 'var(--navy-dark)' }}>📍 {item.regionName || `Region #${item.regionId}`}</strong>
                        <span className="font-mono" style={{ fontWeight: 700, color: isHigh ? '#C2410C' : '#1E40AF' }}>
                          {pct}% Exhausted
                        </span>
                      </div>
                      <div className="progress-bar-bg" style={{ marginBottom: '8px' }}>
                        <div className={`progress-bar-fill ${isHigh ? 'warning' : ''}`} style={{ width: `${pct}%` }} />
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                        <span>Cap: <strong className="font-mono">₹{Number(item.budgetCap || 0).toLocaleString('en-IN')}</strong></span>
                        <span>Used: <strong className="font-mono">₹{Number(item.budgetUsed || 0).toLocaleString('en-IN')}</strong></span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Panel 3: Non-Compliance Incidents per Scheme */}
          <div className="ledger-card" style={{ padding: '24px', marginBottom: 0 }}>
            <div style={{ borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '12px', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.2rem', color: 'var(--navy-dark)' }}>
                3. Non-Compliance Flags by Scheme
              </h3>
              <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', margin: 0 }}>
                Audit flag count tracking procedural anomalies across schemes.
              </p>
            </div>

            {nonCompliance.length === 0 ? (
              <div style={{ padding: '20px', textAlign: 'center', color: 'var(--text-muted)' }}>✓ Zero compliance anomalies reported.</div>
            ) : (
              <div className="table-responsive">
                <table className="gov-table">
                  <thead>
                    <tr>
                      <th>Scheme Title</th>
                      <th style={{ textAlign: 'right' }}>Audit Flag Count</th>
                    </tr>
                  </thead>
                  <tbody>
                    {nonCompliance.map((item, idx) => (
                      <tr key={idx}>
                        <td style={{ fontWeight: 600 }}>{item.schemeName || `Scheme #${item.schemeId}`}</td>
                        <td style={{ textAlign: 'right' }}>
                          <span className="gov-badge" style={{ backgroundColor: '#FEE2E2', color: '#991B1B', borderColor: '#FCA5A5' }}>
                            {item.nonComplianceCount} Flags
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Panel 4: Verification Turnaround Times */}
          <div className="ledger-card" style={{ padding: '24px', marginBottom: 0 }}>
            <div style={{ borderBottom: '2px solid var(--paper-border-dark)', paddingBottom: '12px', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.2rem', color: 'var(--navy-dark)' }}>
                4. Application Turnaround Times
              </h3>
              <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', margin: 0 }}>
                Average processing hours and case volume by status stage.
              </p>
            </div>

            {turnaroundTimes.length === 0 ? (
              <div style={{ padding: '20px', textAlign: 'center', color: 'var(--text-muted)' }}>No turnaround time data available.</div>
            ) : (
              <div className="table-responsive">
                <table className="gov-table">
                  <thead>
                    <tr>
                      <th>Workflow Stage</th>
                      <th style={{ textAlign: 'center' }}>Total Files</th>
                      <th style={{ textAlign: 'right' }}>Avg Turnaround (Hrs)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {turnaroundTimes.map((item, idx) => (
                      <tr key={idx}>
                        <td><span className={`status-pill status-${item.status}`}>{item.status}</span></td>
                        <td className="font-mono" style={{ textAlign: 'center' }}>{item.applicationCount}</td>
                        <td className="font-mono" style={{ textAlign: 'right', fontWeight: 700 }}>
                          {item.averageTurnaroundTimeInHours ? `${Number(item.averageTurnaroundTimeInHours).toFixed(1)} hrs` : 'N/A'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

        </div>
      )}
    </div>
  );
}

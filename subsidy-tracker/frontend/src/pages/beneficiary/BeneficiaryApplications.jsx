import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../api/client';
import { ScoreStamp } from '../../components/ScoreStamp';
import { StatusPill } from '../../components/StatusPill';

export function BeneficiaryApplications() {
  const { user } = useAuth();
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const benId = user?.beneficiaryId || user?.userId || user?.id;

  useEffect(() => {
    if (!benId) return;
    setLoading(true);
    api.getBeneficiaryApplications(benId)
      .then(res => {
        setApplications(res);
        setLoading(false);
      })
      .catch(err => {
        setError('Failed to fetch application records.');
        setLoading(false);
      });
  }, [benId]);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
        <div>
          <span className="gov-badge">REGISTER OF APPLICATIONS</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            My Subsidy & Grant Applications
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Official ledger of submitted grant applications, calculated eligibility scores, and verification stages.
          </p>
        </div>

        <Link to="/beneficiary/apply" className="btn btn-brass">
          ➕ Apply for New Scheme
        </Link>
      </div>

      {loading ? (
        <div className="ledger-card" style={{ padding: '32px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
          Loading beneficiary application register...
        </div>
      ) : error ? (
        <div className="gov-alert gov-alert-error">{error}</div>
      ) : applications.length === 0 ? (
        <div className="ledger-card" style={{ padding: '40px', textAlign: 'center' }}>
          <div style={{ fontSize: '40px', marginBottom: '12px' }}>📋</div>
          <h3 style={{ fontSize: '1.2rem', marginBottom: '8px' }}>No Submitted Applications Found</h3>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '20px', maxWidth: '460px', margin: '0 auto 20px auto' }}>
            You have not submitted any subsidy applications under your citizen account. Browse available government schemes to apply.
          </p>
          <Link to="/beneficiary/apply" className="btn btn-primary">
            Browse Active Schemes & Apply →
          </Link>
        </div>
      ) : (
        <div className="ledger-card">
          <div className="ledger-header">
            <div className="ledger-title">
              <h2>Official Application Ledger ({applications.length} Files)</h2>
            </div>
            <span className="user-role-badge">BENEFICIARY ID: {benId}</span>
          </div>

          <div className="table-responsive">
            <table className="gov-table">
              <thead>
                <tr>
                  <th>Case File Number</th>
                  <th>Scheme Title</th>
                  <th style={{ textAlign: 'center' }}>Eligibility Score</th>
                  <th>Route</th>
                  <th>Current Status Stage</th>
                  <th>Submitted Date</th>
                  <th style={{ textAlign: 'right' }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {applications.map(app => (
                  <tr key={app.id}>
                    <td className="font-mono" style={{ fontWeight: 600, color: 'var(--navy-dark)' }}>
                      {app.caseNumber}
                    </td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{app.schemeName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Category: {app.declaredCategory}</div>
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <ScoreStamp score={app.eligibilityScore} size="sm" />
                    </td>
                    <td>
                      <span className={`route-badge route-${app.routeType}`}>
                        {app.routeType === 'FAST_TRACK' ? '⚡ FAST TRACK' : '⚠️ ESCALATED'}
                      </span>
                    </td>
                    <td>
                      <StatusPill status={app.status} />
                    </td>
                    <td className="font-mono" style={{ fontSize: '0.8rem' }}>
                      {new Date(app.appliedDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <Link to={`/beneficiary/application/${app.id}`} className="btn btn-secondary btn-sm">
                        View Case File →
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

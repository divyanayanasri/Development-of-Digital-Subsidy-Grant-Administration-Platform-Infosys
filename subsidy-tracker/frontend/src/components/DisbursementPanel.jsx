import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export function DisbursementPanel({ applicationId }) {
  const { user } = useAuth();
  const [disbursementData, setDisbursementData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [releasingStageId, setReleasingStageId] = useState(null);
  const [error, setError] = useState('');

  const canRelease = ['FINANCE_APPROVER', 'ADMIN'].includes(user?.role);

  const fetchPlan = () => {
    setLoading(true);
    setError('');
    api.getDisbursementPlan(applicationId)
      .then(res => {
        setDisbursementData(res);
        setLoading(false);
      })
      .catch(err => {
        // 404 or no plan yet
        setDisbursementData(null);
        setLoading(false);
      });
  };

  useEffect(() => {
    if (applicationId) {
      fetchPlan();
    }
  }, [applicationId]);

  const handleCreatePlan = async () => {
    setCreating(true);
    setError('');
    try {
      await api.createDisbursementPlan(applicationId);
      setCreating(false);
      fetchPlan();
    } catch (err) {
      setCreating(false);
      setError(err.message || 'Failed to create disbursement plan.');
    }
  };

  const handleReleaseStage = async (stageId) => {
    setReleasingStageId(stageId);
    setError('');
    try {
      await api.completeDisbursementStage(stageId);
      setReleasingStageId(null);
      fetchPlan();
    } catch (err) {
      setReleasingStageId(null);
      setError(err.message || 'Failed to complete disbursement stage.');
    }
  };

  if (loading) {
    return (
      <div className="ledger-card" style={{ padding: '20px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
        Loading staged disbursement details...
      </div>
    );
  }

  return (
    <div className="ledger-card" style={{ padding: '24px', borderLeft: '4px solid #059669' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', borderBottom: '1px solid var(--paper-border-dark)', paddingBottom: '12px' }}>
        <div>
          <span className="gov-badge" style={{ backgroundColor: '#D1FAE5', color: '#065F46', borderColor: '#6EE7B7' }}>
            MILESTONE 3 — STAGED DISBURSEMENT
          </span>
          <h3 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.3rem', color: 'var(--navy-dark)', marginTop: '4px', margin: 0 }}>
            Fund Release & Milestone Disbursement Ledger
          </h3>
        </div>

        {disbursementData?.plan && (
          <div className="font-mono" style={{ textAlign: 'right' }}>
            <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>TOTAL APPROVED GRANT</span>
            <div style={{ fontSize: '1.25rem', fontWeight: 700, color: '#166534' }}>
              ₹{Number(disbursementData.plan.totalAmount || 0).toLocaleString('en-IN')}
            </div>
          </div>
        )}
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '16px' }}>{error}</div>}

      {!disbursementData || !disbursementData.plan ? (
        <div style={{ textAlign: 'center', padding: '24px', backgroundColor: '#F8FAFC', border: '1px dashed var(--paper-border-dark)', borderRadius: '2px' }}>
          <div style={{ fontSize: '32px', marginBottom: '8px' }}>💳</div>
          <h4 style={{ fontSize: '1.1rem', marginBottom: '6px' }}>No Disbursement Plan Provisioned</h4>
          <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)', marginBottom: '16px', maxWidth: '500px', margin: '0 auto 16px auto' }}>
            This application is approved. Initialize the staged milestone disbursement schedule to enable fund releases.
          </p>
          <button 
            type="button" 
            className="btn btn-brass"
            onClick={handleCreatePlan}
            disabled={creating}
          >
            {creating ? 'Initializing Plan...' : '➕ Create Disbursement Plan'}
          </button>
        </div>
      ) : (
        <div>
          <div className="table-responsive">
            <table className="gov-table">
              <thead>
                <tr>
                  <th>Stage #</th>
                  <th>Milestone Description</th>
                  <th>Share (%)</th>
                  <th>Release Amount</th>
                  <th>Due Date</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {(disbursementData.stages || []).map((stage) => {
                  const isPending = stage.status === 'PENDING';
                  const isReleased = stage.status === 'RELEASED';
                  
                  return (
                    <tr key={stage.id}>
                      <td className="font-mono" style={{ fontWeight: 700 }}>
                        Stage {stage.stageNo || stage.id}
                      </td>
                      <td style={{ fontWeight: 600 }}>
                        {stage.milestoneName || `Milestone Phase ${stage.stageNo}`}
                      </td>
                      <td className="font-mono">{stage.percentage}%</td>
                      <td className="font-mono" style={{ fontWeight: 700, color: '#166534' }}>
                        ₹{Number(stage.amount || 0).toLocaleString('en-IN')}
                      </td>
                      <td className="font-mono" style={{ fontSize: '0.82rem' }}>
                        {stage.dueDate ? new Date(stage.dueDate).toLocaleDateString('en-IN') : 'N/A'}
                      </td>
                      <td>
                        <span className={`status-pill status-${stage.status}`}>
                          {stage.status}
                        </span>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        {isPending && canRelease ? (
                          <button
                            type="button"
                            className="btn btn-success btn-sm"
                            onClick={() => handleReleaseStage(stage.id)}
                            disabled={releasingStageId === stage.id}
                          >
                            {releasingStageId === stage.id ? 'Processing...' : '💸 Complete / Release Fund'}
                          </button>
                        ) : isReleased ? (
                          <span style={{ fontSize: '0.78rem', color: '#166534', fontWeight: 600 }}>
                            ✓ Released {stage.releasedAt ? new Date(stage.releasedAt).toLocaleDateString('en-IN') : ''}
                          </span>
                        ) : (
                          <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>—</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

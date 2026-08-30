import React, { useState, useEffect } from 'react';
import { api } from '../../api/client';

export function AdminAuditLogsPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    setLoading(true);
    api.getAuditLogs()
      .then(res => {
        const list = Array.isArray(res) ? res : [];
        // Sort reverse-chronological if timestamps exist
        list.sort((a, b) => new Date(b.timestamp || 0) - new Date(a.timestamp || 0));
        setLogs(list);
        setLoading(false);
      })
      .catch(err => {
        setError('Failed to fetch audit log ledger.');
        setLoading(false);
      });
  }, []);

  const filteredLogs = logs.filter(item => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      (item.action && item.action.toLowerCase().includes(term)) ||
      (item.entityType && item.entityType.toLowerCase().includes(term)) ||
      (item.userId && String(item.userId).toLowerCase().includes(term)) ||
      (item.details && item.details.toLowerCase().includes(term))
    );
  });

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <span className="gov-badge">DIRECTORATE SYSTEM SECURITY AUDIT</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            Statewide System Audit Trail Ledger
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Immutable reverse-chronological record of user actions, status transitions, scheme parameters, and system events.
          </p>
        </div>

        <div style={{ width: '280px' }}>
          <input 
            type="text"
            className="form-control"
            placeholder="🔍 Search actions, users, entities..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      {loading ? (
        <div className="ledger-card" style={{ padding: '40px', textAlign: 'center', fontFamily: 'var(--font-mono)' }}>
          Loading official audit log register...
        </div>
      ) : (
        <div className="ledger-card">
          <div className="ledger-header">
            <div className="ledger-title">
              <h2>Audit Log Entries ({filteredLogs.length} Records)</h2>
            </div>
            <span className="user-role-badge">ISO 27001 COMPLIANT LEDGER</span>
          </div>

          <div className="table-responsive">
            <table className="gov-table">
              <thead>
                <tr>
                  <th>Log ID</th>
                  <th>Timestamp</th>
                  <th>User ID / Actor</th>
                  <th>Action Executed</th>
                  <th>Entity Type</th>
                  <th>Entity ID</th>
                  <th>Audit Event Details</th>
                </tr>
              </thead>
              <tbody>
                {filteredLogs.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                      No system audit log entries matching criteria.
                    </td>
                  </tr>
                ) : (
                  filteredLogs.map(log => (
                    <tr key={log.id || `${log.timestamp}_${log.action}`}>
                      <td className="font-mono" style={{ fontWeight: 700, color: 'var(--navy-dark)' }}>
                        #{log.id}
                      </td>
                      <td className="font-mono" style={{ fontSize: '0.8rem', whiteSpace: 'nowrap' }}>
                        {log.timestamp ? new Date(log.timestamp).toLocaleString('en-IN') : 'N/A'}
                      </td>
                      <td className="font-mono">
                        User #{log.userId}
                      </td>
                      <td>
                        <span className="gov-badge" style={{ backgroundColor: '#EFF6FF', color: '#1E40AF', borderColor: '#BFDBFE' }}>
                          {log.action}
                        </span>
                      </td>
                      <td className="font-mono" style={{ fontSize: '0.82rem' }}>
                        {log.entityType}
                      </td>
                      <td className="font-mono">
                        #{log.entityId}
                      </td>
                      <td style={{ fontSize: '0.84rem', color: 'var(--text-secondary)' }}>
                        {log.details || 'System event recorded.'}
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

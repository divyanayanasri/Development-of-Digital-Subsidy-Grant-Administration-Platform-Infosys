import React, { useState, useEffect } from 'react';
import { api } from '../../api/client';

export function RegionsRegisterPage() {
  const [regions, setRegions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    budgetCap: 5000000,
    budgetUsed: 0
  });

  const loadRegions = () => {
    setLoading(true);
    api.getRegions()
      .then(res => {
        setRegions(Array.isArray(res) ? res : []);
        setLoading(false);
      })
      .catch(err => {
        setError('Failed to fetch state regions.');
        setLoading(false);
      });
  };

  useEffect(() => {
    loadRegions();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      const payload = {
        name: formData.name,
        budgetCap: parseFloat(formData.budgetCap || 0),
        budgetUsed: parseFloat(formData.budgetUsed || 0)
      };
      await api.createRegion(payload);
      setSubmitting(false);
      setShowModal(false);
      loadRegions();
      alert('New regional jurisdiction registered.');
    } catch (err) {
      setSubmitting(false);
      setError(err.message || 'Failed to create region.');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
        <div>
          <span className="gov-badge">DIRECTORATE REGIONAL JURISDICTIONS</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            State Regions & Budget Allocations
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Manage regional district boundaries and subsidy budget caps.
          </p>
        </div>

        <button type="button" className="btn btn-brass" onClick={() => setShowModal(true)}>
          ➕ Register New Region
        </button>
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      <div className="ledger-card">
        <div className="ledger-header">
          <div className="ledger-title">
            <h2>State Jurisdictions Ledger ({regions.length})</h2>
          </div>
        </div>

        <div className="table-responsive">
          <table className="gov-table">
            <thead>
              <tr>
                <th>Region ID & Title</th>
                <th>Approved Budget Cap</th>
                <th>Committed / Used Funds</th>
                <th>Exhaustion Level</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '24px', fontFamily: 'var(--font-mono)' }}>Loading regions...</td>
                </tr>
              ) : regions.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>No regions registered.</td>
                </tr>
              ) : (
                regions.map(r => {
                  const cap = Number(r.budgetCap || 0);
                  const used = Number(r.budgetUsed || 0);
                  const pct = cap > 0 ? Math.round((used / cap) * 100) : 0;

                  return (
                    <tr key={r.id}>
                      <td>
                        <div style={{ fontWeight: 600, color: 'var(--navy-dark)' }}>{r.name}</div>
                        <div className="font-mono" style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>ID: #{r.id}</div>
                      </td>
                      <td className="font-mono" style={{ fontWeight: 600 }}>₹{cap.toLocaleString('en-IN')}</td>
                      <td className="font-mono" style={{ color: '#166534', fontWeight: 600 }}>₹{used.toLocaleString('en-IN')}</td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <div className="progress-bar-bg" style={{ width: '120px' }}>
                            <div className="progress-bar-fill" style={{ width: `${pct}%` }} />
                          </div>
                          <span className="font-mono" style={{ fontSize: '0.8rem', fontWeight: 700 }}>{pct}%</span>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Region Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Register New Region Jurisdiction</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>&times;</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Region Name <span className="required">*</span></label>
                  <input 
                    type="text"
                    className="form-control"
                    placeholder="e.g. Northern Tribal Belt District"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Approved Budget Cap (₹) <span className="required">*</span></label>
                  <input 
                    type="number"
                    className="form-control font-mono"
                    value={formData.budgetCap}
                    onChange={(e) => setFormData({ ...formData, budgetCap: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-brass" disabled={submitting}>
                  {submitting ? 'Registering...' : 'Register Region'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

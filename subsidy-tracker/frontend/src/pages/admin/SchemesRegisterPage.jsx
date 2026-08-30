import React, { useState, useEffect } from 'react';
import { api } from '../../api/client';

export function SchemesRegisterPage() {
  const [schemes, setSchemes] = useState([]);
  const [regions, setRegions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    minIncome: 0,
    maxIncome: 300000,
    minLandSize: 0,
    categoryAllowed: 'AGRICULTURE',
    grantAmountMin: 50000,
    grantAmountMax: 200000,
    regionId: 1,
    status: 'ACTIVE'
  });

  const loadData = () => {
    setLoading(true);
    Promise.all([
      api.getSchemes().catch(() => []),
      api.getRegions().catch(() => [])
    ]).then(([schRes, regRes]) => {
      setSchemes(Array.isArray(schRes) ? schRes : []);
      setRegions(Array.isArray(regRes) ? regRes : []);
      if (Array.isArray(regRes) && regRes.length > 0) {
        setFormData(prev => ({ ...prev, regionId: regRes[0].id }));
      }
      setLoading(false);
    }).catch(err => {
      setError(err.message || 'Failed to fetch schemes.');
      setLoading(false);
    });
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      const payload = {
        name: formData.name,
        description: formData.description,
        minIncome: parseFloat(formData.minIncome || 0),
        maxIncome: parseFloat(formData.maxIncome || 0),
        minLandSize: parseFloat(formData.minLandSize || 0),
        categoryAllowed: formData.categoryAllowed,
        grantAmountMin: parseFloat(formData.grantAmountMin || 0),
        grantAmountMax: parseFloat(formData.grantAmountMax || 0),
        regionId: parseInt(formData.regionId, 10),
        status: formData.status
      };

      await api.createScheme(payload);
      setSubmitting(false);
      setShowModal(false);
      loadData();
      alert('New subsidy scheme published successfully.');
    } catch (err) {
      setSubmitting(false);
      setError(err.message || 'Failed to create scheme.');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
        <div>
          <span className="gov-badge">DIRECTORATE SCHEMES REGISTER</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            Subsidy & Grant Schemes Directory
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Configure eligibility ceilings, grant amounts, and regional target parameters.
          </p>
        </div>

        <button type="button" className="btn btn-brass" onClick={() => setShowModal(true)}>
          ➕ Add New Scheme
        </button>
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      <div className="ledger-card">
        <div className="ledger-header">
          <div className="ledger-title">
            <h2>Active Gazette Schemes Ledger ({schemes.length})</h2>
          </div>
        </div>

        <div className="table-responsive">
          <table className="gov-table">
            <thead>
              <tr>
                <th>Scheme ID & Name</th>
                <th>Category</th>
                <th>Income Limits</th>
                <th>Min Land</th>
                <th>Grant Amount Range</th>
                <th>Region ID</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '24px', fontFamily: 'var(--font-mono)' }}>Loading schemes register...</td>
                </tr>
              ) : schemes.length === 0 ? (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>No schemes registered.</td>
                </tr>
              ) : schemes.map(s => (
                <tr key={s.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--navy-dark)' }}>{s.name}</div>
                    <div className="font-mono" style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>ID: #{s.id}</div>
                  </td>
                  <td><span className="gov-badge">{s.categoryAllowed || s.category}</span></td>
                  <td className="font-mono">
                    ₹{Number(s.minIncome || 0).toLocaleString('en-IN')} – ₹{Number(s.maxIncome || s.incomeCeiling || 0).toLocaleString('en-IN')}
                  </td>
                  <td className="font-mono">{s.minLandSize || s.maxLandSize || 0} Acres</td>
                  <td className="font-mono" style={{ color: '#166534', fontWeight: 600 }}>
                    ₹{Number(s.grantAmountMin || s.minGrantAmount || 0).toLocaleString('en-IN')} – ₹{Number(s.grantAmountMax || s.maxGrantAmount || 0).toLocaleString('en-IN')}
                  </td>
                  <td className="font-mono">
                    Region #{s.regionId || 1}
                  </td>
                  <td>
                    <span className="status-pill status-APPROVED" style={{ fontSize: '0.7rem' }}>
                      ● {s.status || 'ACTIVE'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Scheme Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-dialog" style={{ maxWidth: '640px' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Add New Government Subsidy Scheme</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>&times;</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Scheme Title <span className="required">*</span></label>
                  <input 
                    type="text"
                    className="form-control"
                    placeholder="e.g. National Solar Pump Installation Subsidy"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Short Description</label>
                  <textarea 
                    className="form-textarea"
                    rows="2"
                    placeholder="Brief description of scheme benefits and targets"
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div className="form-group">
                    <label className="form-label">Allowed Category <span className="required">*</span></label>
                    <select
                      className="form-select"
                      value={formData.categoryAllowed}
                      onChange={(e) => setFormData({ ...formData, categoryAllowed: e.target.value })}
                    >
                      <option value="AGRICULTURE">AGRICULTURE</option>
                      <option value="EDUCATION">EDUCATION</option>
                      <option value="BUSINESS">BUSINESS / MSME</option>
                      <option value="HOUSING">HOUSING</option>
                      <option value="GENERAL">GENERAL</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label">Target Region Jurisdiction <span className="required">*</span></label>
                    <select
                      className="form-select"
                      value={formData.regionId}
                      onChange={(e) => setFormData({ ...formData, regionId: e.target.value })}
                    >
                      {regions.map(r => (
                        <option key={r.id} value={r.id}>{r.name || `Region #${r.id}`}</option>
                      ))}
                      {regions.length === 0 && <option value="1">Central District</option>}
                    </select>
                  </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px' }}>
                  <div className="form-group">
                    <label className="form-label">Min Income (₹)</label>
                    <input 
                      type="number"
                      className="form-control font-mono"
                      value={formData.minIncome}
                      onChange={(e) => setFormData({ ...formData, minIncome: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Max Income (₹) <span className="required">*</span></label>
                    <input 
                      type="number"
                      className="form-control font-mono"
                      value={formData.maxIncome}
                      onChange={(e) => setFormData({ ...formData, maxIncome: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Min Land (Acres)</label>
                    <input 
                      type="number"
                      step="0.1"
                      className="form-control font-mono"
                      value={formData.minLandSize}
                      onChange={(e) => setFormData({ ...formData, minLandSize: e.target.value })}
                    />
                  </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div className="form-group">
                    <label className="form-label">Min Grant Amount (₹) <span className="required">*</span></label>
                    <input 
                      type="number"
                      className="form-control font-mono"
                      value={formData.grantAmountMin}
                      onChange={(e) => setFormData({ ...formData, grantAmountMin: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Max Grant Amount (₹) <span className="required">*</span></label>
                    <input 
                      type="number"
                      className="form-control font-mono"
                      value={formData.grantAmountMax}
                      onChange={(e) => setFormData({ ...formData, grantAmountMax: e.target.value })}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-brass" disabled={submitting}>
                  {submitting ? 'Saving Scheme...' : 'Publish Scheme'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

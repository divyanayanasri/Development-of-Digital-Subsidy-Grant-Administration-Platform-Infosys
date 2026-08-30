import React, { useState, useEffect } from 'react';
import { api } from '../../api/client';

export function UsersRegisterPage() {
  const [users, setUsers] = useState([]);
  const [regions, setRegions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    passwordHash: 'Password123!',
    role: 'FIELD_OFFICER',
    regionId: 1
  });

  const loadData = () => {
    setLoading(true);
    Promise.all([
      api.getUsers().catch(() => []),
      api.getRegions().catch(() => [])
    ]).then(([uRes, rRes]) => {
      setUsers(Array.isArray(uRes) ? uRes : []);
      setRegions(Array.isArray(rRes) ? rRes : []);
      if (Array.isArray(rRes) && rRes.length > 0) {
        setFormData(prev => ({ ...prev, regionId: rRes[0].id }));
      }
      setLoading(false);
    }).catch(err => {
      setError('Failed to fetch user directory.');
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
        email: formData.email,
        passwordHash: formData.passwordHash,
        role: formData.role,
        regionId: parseInt(formData.regionId, 10)
      };

      await api.createUser(payload);
      setSubmitting(false);
      setShowModal(false);
      loadData();
      alert('Officer account provisioned successfully.');
    } catch (err) {
      setSubmitting(false);
      setError(err.message || 'Failed to create user account.');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
        <div>
          <span className="gov-badge">DIRECTORATE PERSONNEL DIRECTORY</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
            Officer Account Provisioning
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
            Provision access for Field Officers, District Collectorate Officers, Finance Approvers, and Admins.
          </p>
        </div>

        <button type="button" className="btn btn-brass" onClick={() => setShowModal(true)}>
          ➕ Provision New Officer
        </button>
      </div>

      {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '20px' }}>{error}</div>}

      <div className="ledger-card">
        <div className="ledger-header">
          <div className="ledger-title">
            <h2>Authorized State Personnel Directory ({users.length})</h2>
          </div>
        </div>

        <div className="table-responsive">
          <table className="gov-table">
            <thead>
              <tr>
                <th>Officer ID & Name</th>
                <th>Official Email</th>
                <th>Assigned Role</th>
                <th>Region Jurisdiction</th>
                <th>Provisioned Date</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '24px', fontFamily: 'var(--font-mono)' }}>Loading users...</td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>No officer accounts provisioned.</td>
                </tr>
              ) : (
                users.map(u => (
                  <tr key={u.id}>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--navy-dark)' }}>{u.name}</div>
                      <div className="font-mono" style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>ID: #{u.id}</div>
                    </td>
                    <td className="font-mono">{u.email}</td>
                    <td>
                      <span className="user-role-badge" style={{ fontWeight: 700 }}>{u.role}</span>
                    </td>
                    <td className="font-mono">
                      Region #{u.regionId || 1}
                    </td>
                    <td className="font-mono" style={{ fontSize: '0.82rem' }}>
                      {u.createdAt ? new Date(u.createdAt).toLocaleDateString('en-IN') : 'Active'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create User Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Provision New Officer Account</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>&times;</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Full Name <span className="required">*</span></label>
                  <input 
                    type="text"
                    className="form-control"
                    placeholder="e.g. Vikramaditya Singh"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Official Email Address <span className="required">*</span></label>
                  <input 
                    type="email"
                    className="form-control font-mono"
                    placeholder="e.g. officer@gov.in"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div className="form-group">
                    <label className="form-label">Designated Role <span className="required">*</span></label>
                    <select
                      className="form-select"
                      value={formData.role}
                      onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                    >
                      <option value="FIELD_OFFICER">FIELD_OFFICER</option>
                      <option value="DISTRICT_OFFICER">DISTRICT_OFFICER</option>
                      <option value="FINANCE_APPROVER">FINANCE_APPROVER</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label">Assign Region <span className="required">*</span></label>
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

                <div className="form-group">
                  <label className="form-label">Initial Security Password <span className="required">*</span></label>
                  <input 
                    type="password"
                    className="form-control"
                    value={formData.passwordHash}
                    onChange={(e) => setFormData({ ...formData, passwordHash: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-brass" disabled={submitting}>
                  {submitting ? 'Provisioning...' : 'Provision Account'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

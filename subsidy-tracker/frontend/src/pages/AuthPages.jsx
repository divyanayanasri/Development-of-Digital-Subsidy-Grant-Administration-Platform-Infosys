import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';

/**
 * 1. Single Unified Login Page
 */
export function LoginPage() {
  const { login, loading, error, setError, user } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // If already authenticated, redirect straight to user's portal
  useEffect(() => {
    if (user) {
      if (user.role === 'BENEFICIARY') {
        navigate('/beneficiary/applications', { replace: true });
      } else if (['FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER'].includes(user.role)) {
        navigate('/officer/queue', { replace: true });
      } else if (user.role === 'ADMIN') {
        navigate('/admin/overview', { replace: true });
      }
    }
  }, [user, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const loggedUser = await login(email, password);
      if (loggedUser.role === 'BENEFICIARY') {
        navigate('/beneficiary/applications');
      } else if (['FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER'].includes(loggedUser.role)) {
        navigate('/officer/queue');
      } else if (loggedUser.role === 'ADMIN') {
        navigate('/admin/overview');
      }
    } catch (err) {
      // Error handled in AuthContext state
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: 'var(--paper-bg)',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '24px'
    }}>
      <div style={{ width: '100%', maxWidth: '440px' }}>
        <div style={{ textAlign: 'center', marginBottom: '20px' }}>
          <div className="official-emblem" style={{ margin: '0 auto 12px auto', width: '56px', height: '56px', fontSize: '28px' }}>
            🏛️
          </div>
          <span className="gov-badge">UNIFIED e-SERVICES LOGIN</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '8px', color: 'var(--navy-dark)' }}>
            Official Portal Sign In
          </h2>
          <p style={{ fontSize: '0.86rem', color: 'var(--text-secondary)' }}>
            Enter your credentials to access citizen services, verification queues, or system administration console.
          </p>
        </div>

        <div className="ledger-card" style={{ padding: '28px' }}>
          {error && <div className="gov-alert gov-alert-error" style={{ marginBottom: '16px' }}>{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email Address <span className="required">*</span></label>
              <input 
                type="email" 
                className="form-control font-mono" 
                placeholder="e.g. officer@gov.in or citizen@gmail.com"
                value={email} 
                onChange={(e) => { setEmail(e.target.value); setError(null); }}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Password <span className="required">*</span></label>
              <input 
                type="password" 
                className="form-control" 
                placeholder="Enter password"
                value={password} 
                onChange={(e) => { setPassword(e.target.value); setError(null); }}
                required
              />
            </div>

            <button type="submit" className="btn btn-brass" style={{ width: '100%', marginTop: '8px', padding: '10px' }} disabled={loading}>
              {loading ? 'Authenticating...' : 'Sign In to Portal →'}
            </button>

            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', textAlign: 'center', marginTop: '12px', fontStyle: 'italic' }}>
              Demo access — full authentication is a planned addition.
            </div>

            <div style={{ marginTop: '20px', paddingTop: '16px', borderTop: '1px solid var(--paper-border-dark)', textAlign: 'center' }}>
              <span style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>New Beneficiary? </span>
              <Link to="/signup" style={{ fontSize: '0.88rem', color: 'var(--brass-hover)', fontWeight: 700 }}>
                Register Here
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

/**
 * 2. Citizen Beneficiary Registration Page
 * Payload matching exact backend endpoint POST /api/beneficiaries:
 * { "name", "email", "password", "aadharNo" (12 digits), "landSize", "annualIncome", "category", "address", "regionId" }
 */
export function BeneficiarySignupPage() {
  const { signup, loading, error, setError } = useAuth();
  const navigate = useNavigate();

  const [regions, setRegions] = useState([]);
  const [localError, setLocalError] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    aadharNo: '',
    landSize: '0.0',
    annualIncome: '',
    category: 'AGRICULTURE',
    address: '',
    regionId: ''
  });

  useEffect(() => {
    api.getRegions()
      .then(res => {
        setRegions(Array.isArray(res) ? res : []);
        if (Array.isArray(res) && res.length > 0) {
          setFormData(prev => ({ ...prev, regionId: res[0].id }));
        }
      })
      .catch(err => {
        console.warn('Failed to load regions:', err);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setLocalError('');
    if (setError) setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.name || !formData.email || !formData.password || !formData.aadharNo || !formData.annualIncome || !formData.address) {
      setLocalError('Please fill in all mandatory registration fields.');
      return;
    }

    if (formData.aadharNo.replace(/\D/g, '').length !== 12) {
      setLocalError('Aadhaar Number must be exactly 12 digits.');
      return;
    }

    try {
      const payload = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        aadharNo: formData.aadharNo.replace(/\D/g, ''),
        landSize: parseFloat(formData.landSize || 0),
        annualIncome: parseFloat(formData.annualIncome || 0),
        category: formData.category,
        address: formData.address,
        regionId: formData.regionId ? parseInt(formData.regionId, 10) : 1
      };

      await signup(payload);
      navigate('/beneficiary/applications');
    } catch (err) {
      setLocalError(err.message || 'Beneficiary registration failed.');
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: 'var(--paper-bg)',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      padding: '32px 16px'
    }}>
      <div style={{ width: '100%', maxWidth: '640px' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <div className="official-emblem" style={{ margin: '0 auto 12px auto', width: '56px', height: '56px', fontSize: '28px' }}>
            🏛️
          </div>
          <span className="gov-badge">BENEFICIARY REGISTRATION</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.75rem', marginTop: '8px', color: 'var(--navy-dark)' }}>
            Register as Citizen Beneficiary
          </h2>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
            Create your citizen account to apply for government grants and track benefit disbursements.
          </p>
        </div>

        <div className="ledger-card" style={{ padding: '28px' }}>
          {(error || localError) && (
            <div className="gov-alert gov-alert-error" style={{ marginBottom: '16px' }}>
              {localError || error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="nav-section-title" style={{ paddingLeft: 0, marginBottom: '12px', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
              1. Account Identity & Credentials
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Full Name <span className="required">*</span></label>
                <input 
                  type="text" 
                  name="name"
                  className="form-control" 
                  placeholder="e.g. Ramesh Kumar Verma"
                  value={formData.name} 
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Email Address <span className="required">*</span></label>
                <input 
                  type="email" 
                  name="email"
                  className="form-control font-mono" 
                  placeholder="e.g. ramesh@gmail.com"
                  value={formData.email} 
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Password <span className="required">*</span></label>
                <input 
                  type="password" 
                  name="password"
                  className="form-control" 
                  placeholder="Create password"
                  value={formData.password} 
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Aadhaar Card Number (12 Digits) <span className="required">*</span></label>
                <input 
                  type="text" 
                  name="aadharNo"
                  className="form-control font-mono" 
                  placeholder="e.g. 123456789012"
                  value={formData.aadharNo} 
                  onChange={handleChange}
                  maxLength="12"
                  required
                />
              </div>
            </div>

            <div className="nav-section-title" style={{ paddingLeft: 0, margin: '20px 0 12px 0', color: 'var(--navy-dark)', borderBottom: '1px solid var(--paper-border-dark)' }}>
              2. Socio-Economic Profile & Jurisdiction
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Annual Household Income (₹) <span className="required">*</span></label>
                <input 
                  type="number" 
                  name="annualIncome"
                  className="form-control font-mono" 
                  placeholder="e.g. 120000"
                  value={formData.annualIncome} 
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Land Size (Acres) <span className="required">*</span></label>
                <input 
                  type="number" 
                  step="0.1"
                  name="landSize"
                  className="form-control font-mono" 
                  placeholder="e.g. 2.5 (enter 0 for landless)"
                  value={formData.landSize} 
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Beneficiary Category <span className="required">*</span></label>
                <select 
                  name="category"
                  className="form-select"
                  value={formData.category}
                  onChange={handleChange}
                >
                  <option value="AGRICULTURE">AGRICULTURE</option>
                  <option value="EDUCATION">EDUCATION</option>
                  <option value="BUSINESS">RURAL BUSINESS</option>
                  <option value="HOUSING">HOUSING</option>
                  <option value="GENERAL">GENERAL</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Resident Region / Jurisdiction <span className="required">*</span></label>
                <select 
                  name="regionId"
                  className="form-select"
                  value={formData.regionId}
                  onChange={handleChange}
                >
                  {regions.map(r => (
                    <option key={r.id} value={r.id}>{r.name || `Region #${r.id}`}</option>
                  ))}
                  {regions.length === 0 && <option value="1">Central District</option>}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Residential Address <span className="required">*</span></label>
              <textarea 
                name="address"
                className="form-textarea"
                rows="2"
                placeholder="Enter full postal residential address"
                value={formData.address}
                onChange={handleChange}
                required
              />
            </div>

            <button type="submit" className="btn btn-brass" style={{ width: '100%', marginTop: '16px', padding: '12px' }} disabled={loading}>
              {loading ? 'Submitting Registration...' : 'Complete Beneficiary Registration →'}
            </button>
          </form>

          <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.88rem' }}>
            <span style={{ color: 'var(--text-secondary)' }}>Already registered? </span>
            <Link to="/login" style={{ color: 'var(--navy-dark)', fontWeight: 700 }}>
              Sign In Here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

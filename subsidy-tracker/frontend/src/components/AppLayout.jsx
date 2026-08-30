import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function AppLayout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [currentTime, setCurrentTime] = useState('');

  useEffect(() => {
    const updateClock = () => {
      const now = new Date();
      setCurrentTime(now.toLocaleDateString('en-IN', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      }).toUpperCase() + ' | ' + now.toLocaleTimeString('en-IN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true
      }));
    };
    updateClock();
    const interval = setInterval(updateClock, 1000);
    return () => clearInterval(interval);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleTitle = (role) => {
    switch (role) {
      case 'BENEFICIARY': return 'Citizen Beneficiary Portal';
      case 'FIELD_OFFICER': return 'Field Verification Officer Workstation';
      case 'DISTRICT_OFFICER': return 'District Collectorate Verification Desk';
      case 'FINANCE_APPROVER': return 'Finance Disbursement Authority';
      case 'ADMIN': return 'Directorate Super Admin Console';
      default: return 'Government e-Services Portal';
    }
  };

  const roleThemeClass = `sidebar-${user?.role || 'OFFICER'}`;

  return (
    <div className="app-container">
      {/* Official Government Tri-Color Top Accent Line */}
      <div className="gov-top-stripe" />

      {/* Top Utility Accessibility & Language Bar */}
      <div className="gov-utility-bar">
        <div>
          🇮🇳 <strong>GOVERNMENT OF INDIA</strong> • PUBLIC SUBSIDY VERIFICATION LEDGER
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <span>Text Size: <strong style={{ cursor: 'pointer' }}>A-</strong> | <strong style={{ cursor: 'pointer' }}>A</strong> | <strong style={{ cursor: 'pointer' }}>A+</strong></span>
          <span>Language: <strong>English</strong></span>
          <span className="font-mono" style={{ color: 'var(--gov-saffron-light)' }}>🕒 {currentTime}</span>
        </div>
      </div>

      <div className="app-workspace">
        {/* Role-Themed Left Sidebar */}
        <aside className={`sidebar ${roleThemeClass}`}>
          <div className="sidebar-header">
            <div className="official-emblem">
              🏛️
            </div>
            <div className="sidebar-header-text">
              <h2>DIRECTORATE OF SUBSIDIES</h2>
              <p>Govt. of India e-Services</p>
            </div>
          </div>

          <nav className="sidebar-nav">
            {user?.role === 'BENEFICIARY' && (
              <>
                <div className="nav-section-title">Citizen Services</div>
                <NavLink to="/beneficiary/applications" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>📋</span> My Applications Register
                </NavLink>
                <NavLink to="/beneficiary/apply" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>➕</span> Apply for New Scheme
                </NavLink>
              </>
            )}

            {['FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER'].includes(user?.role) && (
              <>
                <div className="nav-section-title">Officer Workstation</div>
                <NavLink to="/officer/queue" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>📂</span> Verification Queue Register
                </NavLink>
              </>
            )}

            {user?.role === 'ADMIN' && (
              <>
                <div className="nav-section-title">Directorate Management</div>
                <NavLink to="/admin/overview" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>📊</span> System Overview
                </NavLink>
                <NavLink to="/admin/schemes" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>📜</span> Schemes Register
                </NavLink>
                <NavLink to="/admin/regions" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>🗺️</span> Regions Register
                </NavLink>
                <NavLink to="/admin/users" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>👥</span> Officer Directory
                </NavLink>
                
                <div className="nav-section-title">Statewide Audit & Analytics</div>
                <NavLink to="/admin/compliance" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>⚖️</span> Disbursement Compliance
                </NavLink>
                <NavLink to="/admin/analytics" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>📈</span> Regional Analytics
                </NavLink>
                <NavLink to="/admin/audit-logs" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>🛡️</span> System Audit Trail
                </NavLink>

                <div className="nav-section-title">Statewide Queue</div>
                <NavLink to="/officer/queue" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <span>📂</span> All Verification Queues
                </NavLink>
              </>
            )}
          </nav>

          <div style={{ padding: '16px', borderTop: '1px solid rgba(255,255,255,0.15)', fontSize: '0.72rem', color: 'rgba(255,255,255,0.7)' }}>
            <div>NATIONAL e-GOVERNANCE LEDGER</div>
            <div className="font-mono" style={{ marginTop: '2px' }}>v3.0.0 • PROD BUILD</div>
          </div>
        </aside>

        {/* Main Content Body */}
        <div className="main-content">
          <header className="top-bar">
            <div className="top-bar-title">
              <h1>{getRoleTitle(user?.role)}</h1>
              <span className="gov-badge">OFFICIAL GAZETTE</span>
            </div>

            <div className="top-bar-user">
              <div className="user-info">
                <div className="user-name">{user?.name || 'Authorized Session'}</div>
                <div>
                  <span className="user-role-badge">{user?.role}</span>
                  {user?.region && <span className="user-role-badge" style={{ marginLeft: '4px' }}>📍 {user.region}</span>}
                </div>
              </div>

              <button type="button" className="btn btn-secondary btn-sm" onClick={handleLogout} title="Sign Out Session">
                Sign Out
              </button>
            </div>
          </header>

          <main className="content-body">
            {children}
          </main>
        </div>
      </div>
    </div>
  );
}

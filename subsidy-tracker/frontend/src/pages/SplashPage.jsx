import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function SplashPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // If session exists, auto-redirect immediately
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

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--paper-bg)', display: 'flex', flexDirection: 'column' }}>
      {/* Official Government Tri-Color Top Accent Line */}
      <div className="gov-top-stripe" />

      {/* Top Utility Header Bar */}
      <div className="gov-utility-bar">
        <div>
          🇮🇳 <strong>GOVERNMENT OF INDIA</strong> • PUBLIC SUBSIDY & GRANT DISBURSEMENT PORTAL
        </div>
        <div style={{ display: 'flex', gap: '16px' }}>
          <span>Text Size: <strong>A-</strong> | <strong>A</strong> | <strong>A+</strong></span>
          <span>Language: <strong>English</strong></span>
        </div>
      </div>

      {/* Main Landing Content Container */}
      <div style={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '60px 24px',
        textAlign: 'center'
      }}>
        {/* 1. Government Emblem & Department Name */}
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px', marginBottom: '24px' }}>
          <div className="official-emblem" style={{ width: '80px', height: '80px', fontSize: '42px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            🏛️
          </div>
          <div style={{ fontSize: '0.88rem', color: 'var(--gov-saffron)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700 }}>
            Directorate of Public Subsidy & Disbursement
          </div>
        </div>

        {/* 2. System Title */}
        <h1 style={{
          fontFamily: 'var(--font-serif)',
          fontSize: '2.25rem',
          color: '#0F172A',
          margin: '0 0 16px 0',
          maxWidth: '750px',
          lineHeight: '1.25'
        }}>
          Government Subsidy & Grant Disbursement Tracking System
        </h1>

        {/* 3. One short line describing what it is (single sentence) */}
        <p style={{
          color: 'var(--text-secondary)',
          fontSize: '1.05rem',
          maxWidth: '640px',
          margin: '0 0 36px 0',
          lineHeight: '1.6'
        }}>
          Official direct benefit transfer platform for multi-tier eligibility verification, application tracking, and staged grant disbursements.
        </p>

        {/* 4. Two buttons: Login and Register — nothing else */}
        <div style={{ display: 'flex', gap: '16px', justifyContent: 'center' }}>
          <Link to="/login" className="btn btn-secondary" style={{ padding: '12px 32px', fontSize: '1rem', fontWeight: 700 }}>
            🔑 Login
          </Link>
          <Link to="/signup" className="btn btn-brass" style={{ padding: '12px 32px', fontSize: '1rem', fontWeight: 700 }}>
            📋 Register
          </Link>
        </div>
      </div>

      {/* Official Footer */}
      <footer style={{
        backgroundColor: '#0F172A',
        color: '#94A3B8',
        padding: '20px 32px',
        borderTop: '2px solid #334155',
        fontSize: '0.8rem',
        textAlign: 'center'
      }}>
        <div>Official Direct Benefit Transfer & Verification Portal • Government of India</div>
        <div style={{ marginTop: '4px', fontSize: '0.72rem' }}>All public transactions registered under Digital India e-Governance Standards</div>
      </footer>
    </div>
  );
}

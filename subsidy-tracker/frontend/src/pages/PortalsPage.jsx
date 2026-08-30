import React from 'react';
import { Link } from 'react-router-dom';

export function PortalsPage() {
  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--paper-bg)', display: 'flex', flexDirection: 'column' }}>
      {/* Official Tri-Color Accent Line */}
      <div className="gov-top-stripe" />

      {/* Top Accessibility Bar */}
      <div className="gov-utility-bar">
        <div>
          🇮🇳 <strong>GOVERNMENT OF INDIA</strong> • NATIONAL e-GOVERNANCE SUBSIDY DISBURSEMENT PORTAL
        </div>
        <div style={{ display: 'flex', gap: '16px' }}>
          <span>Text Size: <strong>A-</strong> | <strong>A</strong> | <strong>A+</strong></span>
          <span>Language: <strong>English</strong></span>
        </div>
      </div>

      {/* Formal Header */}
      <header style={{
        backgroundColor: '#0F172A',
        color: '#FFFFFF',
        padding: '18px 32px',
        borderBottom: '3px solid var(--gov-saffron)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div className="official-emblem" style={{ width: '52px', height: '52px', fontSize: '26px' }}>
            🏛️
          </div>
          <div>
            <h1 style={{ fontFamily: 'var(--font-serif)', color: '#FFFFFF', fontSize: '1.35rem', margin: 0 }}>
              DIRECTORATE OF PUBLIC DISBURSEMENT & SUBSIDY VERIFICATION
            </h1>
            <p style={{ fontSize: '0.78rem', color: 'var(--gov-saffron)', textTransform: 'uppercase', letterSpacing: '0.08em', margin: 0, fontWeight: 700 }}>
              Government of India • Unified e-Services Portal
            </p>
          </div>
        </div>

        <div>
          <Link to="/signup" className="btn btn-brass">
            📋 Citizen / Beneficiary Registration
          </Link>
        </div>
      </header>

      {/* Main Content Body */}
      <main style={{ flex: 1, padding: '40px 24px', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '36px' }}>
          <span className="gov-badge" style={{ fontSize: '0.8rem', padding: '4px 12px' }}>NATIONAL GAZETTE REGISTRY PORTAL</span>
          <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '2rem', color: '#0F172A', marginTop: '8px', marginBottom: '8px' }}>
            Select Official Access Portal
          </h2>
          <p style={{ color: 'var(--text-secondary)', maxWidth: '680px', margin: '0 auto', fontSize: '0.95rem' }}>
            Authorized portal selection for citizen applicants, field/district/finance verification officers, and system administration.
          </p>
        </div>

        {/* 3 Distinct Portal Cards */}
        <div className="portal-grid">
          {/* Card 1: Citizen Beneficiary (Emerald Green Theme) */}
          <div className="portal-card">
            <div className="portal-icon">
              👨‍🌾
            </div>
            <h3 style={{ color: '#065F46' }}>Citizen Beneficiary Portal</h3>
            <p>
              Public portal for citizens to browse active subsidy schemes, submit fresh applications with document credentials, and track multi-level verification progress.
            </p>
            <div style={{ marginTop: 'auto', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <Link to="/login" className="btn btn-primary" style={{ width: '100%', backgroundColor: '#065F46' }}>
                Citizen Login →
              </Link>
              <Link to="/signup" style={{ textAlign: 'center', fontSize: '0.82rem', color: '#065F46', fontWeight: 700 }}>
                New Citizen? Register Account
              </Link>
            </div>
          </div>

          {/* Card 2: Officer Verification (Deep Sapphire Blue Theme) */}
          <div className="portal-card card-officer">
            <div className="portal-icon">
              ⚖️
            </div>
            <h3 style={{ color: '#1E3A8A' }}>Officer Verification Portal</h3>
            <p>
              Workstation for Field Verification Officers, District Collectorate Officers, and Finance Approvers to inspect case registers, review document audits, and log actions.
            </p>
            <div style={{ marginTop: 'auto' }}>
              <Link to="/login" className="btn btn-primary" style={{ width: '100%', backgroundColor: '#1E3A8A' }}>
                Officer Portal Login →
              </Link>
            </div>
          </div>

          {/* Card 3: Super Admin (Imperial Purple/Burgundy Theme) */}
          <div className="portal-card card-admin">
            <div className="portal-icon">
              🛡️
            </div>
            <h3 style={{ color: '#701A75' }}>Directorate Super Admin</h3>
            <p>
              Super administrative console for managing subsidy schemes, regional budget allocations, officer account provisioning, and audit reporting across state jurisdictions.
            </p>
            <div style={{ marginTop: 'auto' }}>
              <Link
                to="/login"
                className="btn btn-primary"
                style={{
                  width: '100%',
                  backgroundColor: '#701A75',
                  borderColor: '#4C1D95'
                }}
              >
                Super Admin Console →
              </Link>
            </div>
          </div>
        </div>

      </main>

      {/* Footer */}
      <footer style={{
        backgroundColor: '#0F172A',
        color: '#94A3B8',
        padding: '20px 32px',
        borderTop: '2px solid #334155',
        fontSize: '0.8rem',
        textAlign: 'center'
      }}>
        <div>Official Public Subsidy Disbursement & Verification System • Government of India</div>
        <div style={{ marginTop: '4px', fontSize: '0.72rem' }}>All transactions logged under Digital India Gazette Standards • ISO 27001 Certified e-Service</div>
      </footer>
    </div>
  );
}

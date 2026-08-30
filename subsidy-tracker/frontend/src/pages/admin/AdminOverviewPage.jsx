import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../../api/client';

export function AdminOverviewPage() {
  const [stats, setStats] = useState({
    schemesCount: 0,
    regionsCount: 0,
    usersCount: 0,
    applicationsCount: 0,
    totalBudget: 0,
    usedBudget: 0
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.getSchemes().catch(() => []),
      api.getRegions().catch(() => []),
      api.getUsers().catch(() => []),
      api.getApplicationsQueue('SUBMITTED').catch(() => [])
    ]).then(([schemes, regions, users, apps]) => {
      const totalB = regions.reduce((sum, r) => sum + (r.budgetCap || 0), 0);
      const usedB = regions.reduce((sum, r) => sum + (r.budgetUsed || 0), 0);

      setStats({
        schemesCount: schemes.length,
        regionsCount: regions.length,
        usersCount: users.length,
        applicationsCount: apps.length,
        totalBudget: totalB,
        usedBudget: usedB
      });
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const budgetRatio = stats.totalBudget > 0 ? Math.round((stats.usedBudget / stats.totalBudget) * 100) : 0;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <span className="gov-badge">SUPER ADMIN DIRECTORATE</span>
        <h2 style={{ fontFamily: 'var(--font-serif)', fontSize: '1.6rem', marginTop: '4px', margin: 0 }}>
          System Administration Overview
        </h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', margin: 0 }}>
          Statewide subsidy disbursement metrics, regional budget allocation, and system administration registers.
        </p>
      </div>

      {/* KPI Cards Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px', marginBottom: '28px' }}>
        <div className="ledger-card" style={{ padding: '20px', marginBottom: 0 }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Active Schemes</div>
          <div style={{ fontFamily: 'var(--font-serif)', fontSize: '2.2rem', color: 'var(--navy-dark)', fontWeight: 700, margin: '4px 0' }}>
            {loading ? '...' : stats.schemesCount}
          </div>
          <Link to="/admin/schemes" style={{ fontSize: '0.8rem', color: 'var(--brass-hover)', fontWeight: 600 }}>Manage Schemes Register →</Link>
        </div>

        <div className="ledger-card" style={{ padding: '20px', marginBottom: 0 }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>State Regions</div>
          <div style={{ fontFamily: 'var(--font-serif)', fontSize: '2.2rem', color: 'var(--navy-dark)', fontWeight: 700, margin: '4px 0' }}>
            {loading ? '...' : stats.regionsCount}
          </div>
          <Link to="/admin/regions" style={{ fontSize: '0.8rem', color: 'var(--brass-hover)', fontWeight: 600 }}>Manage Regional Budgets →</Link>
        </div>

        <div className="ledger-card" style={{ padding: '20px', marginBottom: 0 }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Provisioned Personnel</div>
          <div style={{ fontFamily: 'var(--font-serif)', fontSize: '2.2rem', color: 'var(--navy-dark)', fontWeight: 700, margin: '4px 0' }}>
            {loading ? '...' : stats.usersCount}
          </div>
          <Link to="/admin/users" style={{ fontSize: '0.8rem', color: 'var(--brass-hover)', fontWeight: 600 }}>Manage Officer Accounts →</Link>
        </div>

        <div className="ledger-card" style={{ padding: '20px', marginBottom: 0 }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Submitted Applications</div>
          <div style={{ fontFamily: 'var(--font-serif)', fontSize: '2.2rem', color: 'var(--navy-dark)', fontWeight: 700, margin: '4px 0' }}>
            {loading ? '...' : stats.applicationsCount}
          </div>
          <Link to="/officer/queue" style={{ fontSize: '0.8rem', color: 'var(--brass-hover)', fontWeight: 600 }}>View Verification Queues →</Link>
        </div>
      </div>

      {/* Statewide Budget Utilization Card */}
      <div className="ledger-card" style={{ padding: '24px' }}>
        <div className="ledger-header" style={{ padding: 0, backgroundColor: 'transparent', borderBottom: 'none', marginBottom: '16px' }}>
          <div className="ledger-title">
            <h2>Statewide Subsidy Budget Allocation & Utilization</h2>
          </div>
          <span className="font-mono" style={{ fontWeight: 700, color: 'var(--navy-dark)' }}>{budgetRatio}% Disbursed</span>
        </div>

        <div style={{ marginBottom: '16px' }}>
          <div className="progress-bar-bg" style={{ height: '14px' }}>
            <div className="progress-bar-fill" style={{ width: `${budgetRatio}%` }} />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
          <div style={{ backgroundColor: '#F8FAFC', padding: '16px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total Approved State Budget Pool</span>
            <div className="font-mono" style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--navy-dark)', marginTop: '4px' }}>
              ₹{stats.totalBudget.toLocaleString('en-IN')}
            </div>
          </div>

          <div style={{ backgroundColor: '#F8FAFC', padding: '16px', border: '1px solid var(--paper-border)', borderRadius: '2px' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total Funds Committed / Disbursed</span>
            <div className="font-mono" style={{ fontSize: '1.4rem', fontWeight: 700, color: '#166534', marginTop: '4px' }}>
              ₹{stats.usedBudget.toLocaleString('en-IN')}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

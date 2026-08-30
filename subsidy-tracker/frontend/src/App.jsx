import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AppLayout } from './components/AppLayout';

import { SplashPage } from './pages/SplashPage';
import { LoginPage, BeneficiarySignupPage } from './pages/AuthPages';

import { BeneficiaryApplications } from './pages/beneficiary/BeneficiaryApplications';
import { ApplySchemePage } from './pages/beneficiary/ApplySchemePage';
import { ApplicationDetailPage } from './pages/beneficiary/ApplicationDetailPage';

import { OfficerQueuePage } from './pages/officer/OfficerQueuePage';
import { CaseDetailPage } from './pages/officer/CaseDetailPage';

import { AdminOverviewPage } from './pages/admin/AdminOverviewPage';
import { SchemesRegisterPage } from './pages/admin/SchemesRegisterPage';
import { RegionsRegisterPage } from './pages/admin/RegionsRegisterPage';
import { UsersRegisterPage } from './pages/admin/UsersRegisterPage';
import { AdminCompliancePage } from './pages/admin/AdminCompliancePage';
import { AdminAnalyticsPage } from './pages/admin/AdminAnalyticsPage';
import { AdminAuditLogsPage } from './pages/admin/AdminAuditLogsPage';

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public & Authentication Routes */}
          <Route path="/" element={<SplashPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/login/*" element={<Navigate to="/login" replace />} />
          <Route path="/portals" element={<Navigate to="/login" replace />} />
          <Route path="/signup" element={<BeneficiarySignupPage />} />

          {/* Citizen Beneficiary Portal Routes */}
          <Route path="/beneficiary/*" element={
            <ProtectedRoute allowedRoles={['BENEFICIARY']}>
              <AppLayout>
                <Routes>
                  <Route path="applications" element={<BeneficiaryApplications />} />
                  <Route path="apply" element={<ApplySchemePage />} />
                  <Route path="application/:id" element={<ApplicationDetailPage />} />
                  <Route path="*" element={<Navigate to="applications" replace />} />
                </Routes>
              </AppLayout>
            </ProtectedRoute>
          } />

          {/* Verification Officer Portal Routes */}
          <Route path="/officer/*" element={
            <ProtectedRoute allowedRoles={['FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN']}>
              <AppLayout>
                <Routes>
                  <Route path="queue" element={<OfficerQueuePage />} />
                  <Route path="*" element={<Navigate to="queue" replace />} />
                </Routes>
              </AppLayout>
            </ProtectedRoute>
          } />

          {/* Standalone Shareable Case File Detail View */}
          <Route path="/case/:id" element={
            <ProtectedRoute allowedRoles={['FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN', 'BENEFICIARY']}>
              <AppLayout>
                <CaseDetailPage />
              </AppLayout>
            </ProtectedRoute>
          } />

          {/* Super Admin Console Routes */}
          <Route path="/admin/*" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AppLayout>
                <Routes>
                  <Route path="overview" element={<AdminOverviewPage />} />
                  <Route path="schemes" element={<SchemesRegisterPage />} />
                  <Route path="regions" element={<RegionsRegisterPage />} />
                  <Route path="users" element={<UsersRegisterPage />} />
                  <Route path="compliance" element={<AdminCompliancePage />} />
                  <Route path="analytics" element={<AdminAnalyticsPage />} />
                  <Route path="audit-logs" element={<AdminAuditLogsPage />} />
                  <Route path="*" element={<Navigate to="overview" replace />} />
                </Routes>
              </AppLayout>
            </ProtectedRoute>
          } />

          {/* Catch-all fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

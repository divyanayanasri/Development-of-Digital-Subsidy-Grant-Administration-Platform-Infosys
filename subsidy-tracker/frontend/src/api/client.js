/**
 * Production API Client — Real Backend Communication Only
 * Zero mock data, zero silent try/catch fallbacks.
 */

async function request(endpoint, options = {}) {
  const token = localStorage.getItem('gov_token');
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers
  };

  const res = await fetch(endpoint, { ...options, headers });
  
  if (!res.ok) {
    let errorMsg = `HTTP Error ${res.status}: ${res.statusText}`;
    try {
      const errorData = await res.json();
      if (errorData.message) {
        errorMsg = errorData.message;
      } else if (typeof errorData === 'string') {
        errorMsg = errorData;
      }
    } catch (e) {
      // Body not JSON
    }
    throw new Error(errorMsg);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : {};
}

export const api = {
  // Auth
  login: (email, password) => request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password })
  }),
  // Users (Officers & Admins)
  getUsers: () => request('/api/users'),
  createUser: (data) => request('/api/users', { method: 'POST', body: JSON.stringify(data) }),

  // Regions & Schemes
  getRegions: () => request('/api/regions'),
  createRegion: (data) => request('/api/admin/regions', { method: 'POST', body: JSON.stringify(data) }),
  getSchemes: () => request('/api/admin/schemes'),
  createScheme: (data) => request('/api/admin/schemes', { method: 'POST', body: JSON.stringify(data) }),
  updateScheme: (id, data) => request(`/api/admin/schemes/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  // Beneficiaries
  registerBeneficiary: (data) => request('/api/beneficiaries', { method: 'POST', body: JSON.stringify(data) }),
  getBeneficiaryApplications: (benId) => request(`/api/beneficiaries/${benId}/applications`),
  uploadBeneficiaryDocument: (benId, data) => request(`/api/beneficiaries/${benId}/documents`, { method: 'POST', body: JSON.stringify(data) }),
  getBeneficiaryDocuments: (benId) => request(`/api/beneficiaries/${benId}/documents`),
  checkBeneficiaryDocuments: (benId) => request(`/api/beneficiaries/${benId}/documents/check`),

  // Applications & Workflow
  submitApplication: (beneficiaryId, schemeId) => request('/api/applications', { 
    method: 'POST', 
    body: JSON.stringify({ beneficiaryId, schemeId }) 
  }),
  getApplicationDetail: (id) => request(`/api/applications/${id}`),
  getApplicationHistory: (id) => request(`/api/applications/${id}/history`),
  getApplicationsQueue: (status) => request(`/api/applications/queue/${status}`),
  transitionApplication: (id, payload) => request(`/api/applications/${id}/transition`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  }),

  // Milestone 3 — Staged Disbursement & Compliance
  getDisbursementPlan: (applicationId) => request(`/api/applications/${applicationId}/disbursement-plan`),
  createDisbursementPlan: (applicationId) => request(`/disbursement/create/${applicationId}`, { method: 'POST' }),
  completeDisbursementStage: (stageId) => request(`/api/disbursement/${stageId}/complete`, { method: 'PUT' }),
  getNonCompliantDisbursements: () => request('/api/disbursement/non-compliant'),

  // Milestone 4 — Analytics & Reporting
  getFundUtilization: () => request('/api/analytics/fund-utilization'),
  getBudgetExhaustion: () => request('/api/analytics/budget-exhaustion'),
  getAnalyticsNonCompliance: () => request('/api/analytics/non-compliance'),
  getTurnaroundTimes: () => request('/api/analytics/turnaround-times'),
  getReportExportUrl: (format) => `/api/reports/export?format=${format}`,

  // Milestone 4/5 — Audit Trail
  getAuditLogs: () => request('/api/analytics/audit-logs')
};

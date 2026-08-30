/**
 * State transition matrix for Government Subsidy Disbursement Tracking System
 */

export const TRANSITION_MATRIX = {
  FAST_TRACK: {
    SUBMITTED: {
      role: 'FIELD_OFFICER',
      next: ['FIELD_REVIEW', 'REJECTED']
    },
    FIELD_REVIEW: {
      role: 'FINANCE_APPROVER',
      next: ['FINANCE_REVIEW']
    },
    FINANCE_REVIEW: {
      role: 'FINANCE_APPROVER',
      next: ['APPROVED', 'REJECTED', 'RE_VERIFICATION']
    },
    RE_VERIFICATION: {
      role: 'FIELD_OFFICER',
      next: ['FIELD_REVIEW']
    }
  },
  ESCALATED: {
    SUBMITTED: {
      role: 'FIELD_OFFICER',
      next: ['FIELD_REVIEW', 'REJECTED']
    },
    FIELD_REVIEW: {
      role: 'DISTRICT_OFFICER',
      next: ['DISTRICT_REVIEW', 'RE_VERIFICATION', 'REJECTED']
    },
    DISTRICT_REVIEW: {
      role: 'FINANCE_APPROVER',
      next: ['FINANCE_REVIEW']
    },
    FINANCE_REVIEW: {
      role: 'FINANCE_APPROVER',
      next: ['APPROVED', 'REJECTED', 'RE_VERIFICATION']
    },
    RE_VERIFICATION: {
      role: 'FIELD_OFFICER',
      next: ['FIELD_REVIEW']
    }
  }
}

export const ACTION_LABELS = {
  FIELD_REVIEW: 'Clear to Field Review',
  DISTRICT_REVIEW: 'Clear to District Review',
  FINANCE_REVIEW: 'Submit for Finance Approval',
  APPROVED: 'Approve Grant Disbursement',
  REJECTED: 'Reject Case File',
  RE_VERIFICATION: 'Send Back for Re-verification'
}

export const ACTION_VARIANTS = {
  FIELD_REVIEW: 'btn-primary',
  DISTRICT_REVIEW: 'btn-primary',
  FINANCE_REVIEW: 'btn-brass',
  APPROVED: 'btn-success',
  REJECTED: 'btn-danger',
  RE_VERIFICATION: 'btn-warning'
}

/**
 * Returns allowed next statuses for a given routeType, current status, and officer role.
 */
export function getValidNextStatuses(routeType, currentStatus, officerRole) {
  if (!routeType || !currentStatus || !officerRole) return [];
  const routeConfig = TRANSITION_MATRIX[routeType];
  if (!routeConfig) return [];
  const statusConfig = routeConfig[currentStatus];
  if (!statusConfig) return [];

  // Super Admin can bypass role check or see all available actions for testing
  if (officerRole === 'ADMIN') {
    return statusConfig.next;
  }

  if (statusConfig.role === officerRole) {
    return statusConfig.next;
  }

  return [];
}

/**
 * Returns queues visible to an officer role
 */
export function getOfficerQueueTabs(role) {
  switch (role) {
    case 'FIELD_OFFICER':
      return [
        { id: 'SUBMITTED', label: 'Pending Initial Verification' },
        { id: 'RE_VERIFICATION', label: 'Returned for Re-Verification' },
        { id: 'ALL', label: 'All Regional Cases' }
      ];
    case 'DISTRICT_OFFICER':
      return [
        { id: 'FIELD_REVIEW', label: 'Pending District Review (Escalated)' },
        { id: 'ALL', label: 'All District Cases' }
      ];
    case 'FINANCE_APPROVER':
      return [
        { id: 'FINANCE_QUEUE', label: 'Pending Finance Review (Fast & Escalated)' },
        { id: 'FINANCE_REVIEW', label: 'Under Final Disbursement Review' },
        { id: 'ALL', label: 'All Financial Cases' }
      ];
    case 'ADMIN':
      return [
        { id: 'ALL', label: 'All State Applications' },
        { id: 'SUBMITTED', label: 'Submitted' },
        { id: 'FIELD_REVIEW', label: 'Field Review' },
        { id: 'DISTRICT_REVIEW', label: 'District Review' },
        { id: 'FINANCE_REVIEW', label: 'Finance Review' },
        { id: 'APPROVED', label: 'Disbursed/Approved' },
        { id: 'REJECTED', label: 'Rejected' },
        { id: 'RE_VERIFICATION', label: 'Re-Verification' }
      ];
    default:
      return [{ id: 'ALL', label: 'All Cases' }];
  }
}

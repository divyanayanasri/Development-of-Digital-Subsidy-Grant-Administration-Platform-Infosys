import React from 'react';

const STATUS_LABELS = {
  SUBMITTED: 'SUBMITTED FOR VERIFICATION',
  FIELD_REVIEW: 'FIELD OFFICER REVIEW',
  DISTRICT_REVIEW: 'DISTRICT REVIEW',
  FINANCE_REVIEW: 'FINANCE APPROVAL QUEUE',
  APPROVED: 'DISBURSEMENT APPROVED',
  REJECTED: 'CASE REJECTED',
  RE_VERIFICATION: 'RE-VERIFICATION REQUIRED'
};

export function StatusPill({ status }) {
  const label = STATUS_LABELS[status] || status;
  return (
    <span className={`status-pill status-${status}`}>
      ● {label}
    </span>
  );
}

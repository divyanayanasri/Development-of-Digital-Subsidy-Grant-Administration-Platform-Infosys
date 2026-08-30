import React, { useState } from 'react';
import { ACTION_LABELS, ACTION_VARIANTS } from '../utils/transitions';

export function VerificationModal({ isOpen, onClose, onConfirm, targetStatus, caseNumber }) {
  const [remarks, setRemarks] = useState('');
  const [error, setError] = useState('');

  if (!isOpen || !targetStatus) return null;

  const actionLabel = ACTION_LABELS[targetStatus] || targetStatus;
  const btnClass = ACTION_VARIANTS[targetStatus] || 'btn-primary';

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!remarks.trim() || remarks.trim().length < 5) {
      setError('Official officer remarks are mandatory (minimum 5 characters).');
      return;
    }
    setError('');
    onConfirm(targetStatus, remarks.trim());
    setRemarks('');
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Verification Action: {actionLabel}</h3>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="gov-alert gov-alert-warning" style={{ marginBottom: '16px' }}>
              <span>⚠️</span>
              <div>
                <strong>Official Audit Notice:</strong> You are about to record a formal procedural action on Case File <span className="font-mono">{caseNumber}</span>. This action will be permanently logged under your officer session credentials.
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">
                Target Transition Stage
              </label>
              <input 
                type="text" 
                className="form-control font-mono" 
                value={`${targetStatus} (${actionLabel})`} 
                disabled 
                style={{ backgroundColor: '#F1F5F9', fontWeight: 600 }}
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                Official Verification Remarks <span className="required">*</span>
              </label>
              <textarea
                className="form-textarea"
                rows={4}
                placeholder="Enter detailed officer remarks, field findings, document verification details, or reasons for decision..."
                value={remarks}
                onChange={(e) => {
                  setRemarks(e.target.value);
                  if (error) setError('');
                }}
                required
              />
              <span className="form-help">Must provide official justification for audit records.</span>
            </div>

            {error && (
              <div className="gov-alert gov-alert-error" style={{ marginTop: '8px' }}>
                {error}
              </div>
            )}
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className={`btn ${btnClass}`}>
              Confirm & Log {actionLabel}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

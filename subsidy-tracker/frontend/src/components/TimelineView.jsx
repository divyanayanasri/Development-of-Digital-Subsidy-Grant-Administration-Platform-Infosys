import React from 'react';
import { StatusPill } from './StatusPill';

export function TimelineView({ history = [] }) {
  if (!history || history.length === 0) {
    return <div className="text-muted" style={{ fontStyle: 'italic' }}>No audit trail entries recorded yet.</div>;
  }

  return (
    <div className="timeline">
      {history.map((item, idx) => {
        const dateStr = item.timestamp ? new Date(item.timestamp).toLocaleString('en-IN', {
          dateStyle: 'medium',
          timeStyle: 'short'
        }) : 'Date unavailable';

        return (
          <div key={item.id || idx} className="timeline-item">
            <div className="timeline-dot"></div>
            <div className="timeline-content">
              <div className="timeline-header">
                <div>
                  <span className="timeline-actor">{item.officerName || 'Official'}</span>
                  {item.officerRole && (
                    <span className="user-role-badge" style={{ marginLeft: '8px' }}>
                      {item.officerRole}
                    </span>
                  )}
                </div>
                <span className="timeline-date">{dateStr}</span>
              </div>
              <div style={{ marginBottom: '6px' }}>
                <StatusPill status={item.action} />
              </div>
              {item.remarks && (
                <div className="timeline-remarks">
                  "{item.remarks}"
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}

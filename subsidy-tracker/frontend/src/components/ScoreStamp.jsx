import React from 'react';

export function ScoreStamp({ score, routeType, size = 'md' }) {
  const numScore = Number(score) || 0;
  
  let stampClass = 'stamp-medium';
  if (numScore >= 75) stampClass = 'stamp-high';
  else if (numScore < 55) stampClass = 'stamp-low';

  return (
    <div className="stamp-container">
      <div className={`stamp-badge ${size === 'sm' ? 'sm' : ''} ${stampClass}`}>
        <span className="stamp-score">{numScore}</span>
        <span className="stamp-label">VERIFIED</span>
      </div>
      {routeType && (
        <span className={`route-badge route-${routeType}`} style={{ marginTop: '6px' }}>
          {routeType === 'FAST_TRACK' ? '⚡ FAST TRACK' : '⚠️ ESCALATED'}
        </span>
      )}
    </div>
  );
}

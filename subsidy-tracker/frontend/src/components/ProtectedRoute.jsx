import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function ProtectedRoute({ allowedRoles, children }) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    return <Navigate to="/portals" state={{ from: location }} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    // Redirect to default page based on actual role
    if (user.role === 'BENEFICIARY') return <Navigate to="/beneficiary/applications" replace />;
    if (['FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER'].includes(user.role)) return <Navigate to="/officer/queue" replace />;
    if (user.role === 'ADMIN') return <Navigate to="/admin/overview" replace />;
    return <Navigate to="/portals" replace />;
  }

  return children;
}

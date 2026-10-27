import { http } from './client';
import { ENDPOINTS } from './endpoints';

export const adminApi = {
  getPendingSellers: (signal) => http.get(ENDPOINTS.admin.pendingSellers, { signal }),
  reviewSeller: (id, approved) =>
    http.post(approved ? ENDPOINTS.admin.approveSeller(id) : ENDPOINTS.admin.declineSeller(id)),
  getPendingComments: (signal) => http.get(ENDPOINTS.admin.pendingComments, { signal }),
  reviewComment: (id, approved) =>
    http.post(approved ? ENDPOINTS.admin.approveComment(id) : ENDPOINTS.admin.declineComment(id)),
};

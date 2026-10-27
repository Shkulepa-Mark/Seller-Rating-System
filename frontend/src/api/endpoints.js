export const ENDPOINTS = {
  auth: {
    login: '/auth/login',
    register: '/auth/register',
    confirm: '/auth/confirm',
    forgotPassword: '/auth/forgot_password',
    resetPassword: '/auth/reset',
    checkCode: '/auth/check_code',
  },
  games: '/games',
  sellers: {
    collection: '/sellers',
    byId: (id) => `/sellers/${id}`,
    requests: '/sellers/requests',
  },
  comments: {
    collection: (sellerId) => `/sellers/${sellerId}/comments`,
    byId: (sellerId, commentId) => `/sellers/${sellerId}/comments/${commentId}`,
  },
  gameObjects: {
    collection: '/game-objects',
    byId: (id) => `/game-objects/${id}`,
  },
  admin: {
    pendingSellers: '/admin/users/pending',
    approveSeller: (id) => `/admin/users/${id}/approve`,
    declineSeller: (id) => `/admin/users/${id}/decline`,
    pendingComments: '/admin/comments/pending',
    approveComment: (id) => `/admin/comments/${id}/approve`,
    declineComment: (id) => `/admin/comments/${id}/decline`,
  },
};

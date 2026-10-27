import { http } from './client';
import { ENDPOINTS } from './endpoints';

export const authApi = {
  login: (credentials) => http.post(ENDPOINTS.auth.login, credentials),
  register: (payload) => http.post(ENDPOINTS.auth.register, payload),
  confirmEmail: (token) => http.get(ENDPOINTS.auth.confirm, { params: { token } }),
  forgotPassword: (email) => http.post(ENDPOINTS.auth.forgotPassword, { email }),
  checkResetCode: (code) => http.get(ENDPOINTS.auth.checkCode, { params: { code } }),
  resetPassword: ({ code, newPassword }) => http.post(ENDPOINTS.auth.resetPassword, { code, newPassword }),
};

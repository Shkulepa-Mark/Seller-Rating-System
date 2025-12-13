import { sessionStorageService } from '../utils/storage';

const BASE_URL = (import.meta.env.VITE_API_URL ?? '/api').replace(/\/+$/, '');

const FALLBACK_MESSAGES = {
  403: 'You do not have permission to do this.',
  404: 'The requested resource was not found.',
};
const DEFAULT_MESSAGE = 'Something went wrong. Please try again.';

let unauthorizedHandler = null;

export class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

function buildUrl(path, params) {
  const url = new URL(`${BASE_URL}${path}`, window.location.origin);
  Object.entries(params ?? {}).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.set(key, value);
    }
  });
  return url;
}

async function parseBody(response) {
  const contentType = response.headers.get('content-type') ?? '';
  if (response.status === 204 || !contentType.includes('application/json')) {
    return null;
  }
  return response.json();
}

function extractMessage(data, status) {
  if (typeof data === 'string' && data) {
    return data;
  }
  return data?.message ?? FALLBACK_MESSAGES[status] ?? DEFAULT_MESSAGE;
}

async function request(path, { method = 'GET', body, params, signal } = {}) {
  const session = sessionStorageService.read();
  const headers = { Accept: 'application/json' };

  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }
  if (session?.token) {
    headers.Authorization = `Bearer ${session.token}`;
  }

  const response = await fetch(buildUrl(path, params), {
    method,
    headers,
    signal,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const data = await parseBody(response);

  if (!response.ok) {
    if (response.status === 401 && session?.token) {
      unauthorizedHandler?.();
    }
    throw new ApiError(extractMessage(data, response.status), response.status);
  }

  return data;
}

export const http = {
  get: (path, options) => request(path, { ...options, method: 'GET' }),
  post: (path, body, options) => request(path, { ...options, method: 'POST', body }),
  put: (path, body, options) => request(path, { ...options, method: 'PUT', body }),
  delete: (path, options) => request(path, { ...options, method: 'DELETE' }),
};

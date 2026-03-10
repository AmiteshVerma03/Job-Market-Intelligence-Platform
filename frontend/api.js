/* ══════════════════════════════════════════
   api.js — Central API client
   All backend calls go through here.
   Token is read from localStorage automatically.
══════════════════════════════════════════ */

const API_BASE = 'http://localhost:8080';

// ── Token helpers ──────────────────────────
function getToken()       { return localStorage.getItem('jwtToken'); }
function setToken(t)      { localStorage.setItem('jwtToken', t); }
function removeToken()    { localStorage.removeItem('jwtToken'); }
function getEmail()       { return localStorage.getItem('userEmail'); }
function setEmail(e)      { localStorage.setItem('userEmail', e); }
function removeEmail()    { localStorage.removeItem('userEmail'); }
function getRole()        { return localStorage.getItem('userRole'); }
function setRole(r)       { localStorage.setItem('userRole', r); }
function removeRole()     { localStorage.removeItem('userRole'); }

// Decode JWT payload (no verification — just for display)
function decodeToken(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch { return null; }
}

// ── Core request helper ────────────────────
async function apiRequest(path, options = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  // 401 = token expired → send back to login
  if (res.status === 401) {
    removeToken(); removeEmail(); removeRole();
    window.location.href = 'index.html';
    throw new Error('Session expired. Please log in again.');
  }

  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  if (!res.ok) {
    const msg = (data && data.message) ? data.message : (typeof data === 'string' ? data : 'Request failed');
    throw new Error(msg);
  }

  return data;
}

// ══════════════════════════════════════════
//  AUTH ENDPOINTS
// ══════════════════════════════════════════
const Auth = {
  async register(name, email, password) {
    return apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password })
    });
  },

  async login(email, password) {
    // Login returns a raw JWT string
    return apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
  }
};

// ══════════════════════════════════════════
//  JOB ENDPOINTS
// ══════════════════════════════════════════
const Jobs = {
  async count() {
    return apiRequest('/jobs/count');
  },

  async getAll(page = 0, size = 12) {
    return apiRequest(`/jobs?page=${page}&size=${size}`);
  },

  async searchByLocation(location) {
    return apiRequest(`/jobs/search/location?location=${encodeURIComponent(location)}`);
  },

  async searchByCompany(company) {
    return apiRequest(`/jobs/search/company?company=${encodeURIComponent(company)}`);
  },

  async searchBySkill(skill) {
    return apiRequest(`/jobs/search/skill?skill=${encodeURIComponent(skill)}`);
  }
};

// ══════════════════════════════════════════
//  ANALYTICS ENDPOINTS
// ══════════════════════════════════════════
const Analytics = {
  async topSkills(limit = 10) {
    return apiRequest(`/analytics/top-skills?limit=${limit}`);
  },

  async topSkillsByLocation(location, limit = 10) {
    return apiRequest(`/analytics/top-skills-by-location/${encodeURIComponent(location)}?limit=${limit}`);
  },

  async topCompanies() {
    return apiRequest('/analytics/top-companies');
  },

  async salaryBySkill(skill) {
    return apiRequest(`/analytics/salary-by-skill?skill=${encodeURIComponent(skill)}`);
  },

  async averageSalaryByLocation() {
    return apiRequest('/analytics/average-salary-by-location');
  }
};

// ══════════════════════════════════════════
//  ADMIN ENDPOINTS
// ══════════════════════════════════════════
const Admin = {
  async runScraper() {
    return apiRequest('/admin/run-scraper', { method: 'POST' });
  }
};

/**
 * ExamGuard AI - Core API & Session Utility
 */

const API_BASE = '/api';

const Session = {
  getToken() {
    return localStorage.getItem('examguard_token');
  },
  getUser() {
    const u = localStorage.getItem('examguard_user');
    return u ? JSON.parse(u) : null;
  },
  setUser(token, user) {
    localStorage.setItem('examguard_token', token);
    localStorage.setItem('examguard_user', JSON.stringify(user));
  },
  clear() {
    localStorage.removeItem('examguard_token');
    localStorage.removeItem('examguard_user');
  },
  requireRole(expectedRole) {
    const user = this.getUser();
    const token = this.getToken();
    if (!token || !user) {
      window.location.href = '/login.html';
      return null;
    }
    if (user.role !== expectedRole) {
      alert('Access Denied: You do not have permissions for this portal.');
      if (user.role === 'ADMIN') window.location.href = '/admin-dashboard.html';
      else if (user.role === 'PROFESSOR') window.location.href = '/professor-dashboard.html';
      else window.location.href = '/student-dashboard.html';
      return null;
    }
    return user;
  }
};

async function apiRequest(endpoint, method = 'GET', body = null) {
  const headers = {
    'Content-Type': 'application/json'
  };

  const token = Session.getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const options = {
    method,
    headers
  };

  if (body) {
    options.body = JSON.stringify(body);
  }

  try {
    const res = await fetch(`${API_BASE}${endpoint}`, options);
    
    if (res.status === 401) {
      Session.clear();
      window.location.href = '/login.html';
      throw new Error('Session expired. Please log in again.');
    }
    if (res.status === 403) {
      throw new Error('Access Denied: You do not have permission for this resource.');
    }

    let data;
    try {
      data = await res.json();
    } catch (e) {
      if (!res.ok) throw new Error(`HTTP Error ${res.status}`);
      return null;
    }

    if (!res.ok || !data.success) {
      throw new Error(data.message || 'Request failed');
    }
    return data.data;
  } catch (err) {
    console.error('API Error:', err);
    throw err;
  }
}

function showToast(message, isError = false) {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = 'toast';
  if (isError) {
    toast.style.backgroundColor = '#1c5551';
    toast.style.borderLeft = '4px solid #FFF8B0';
  }
  toast.innerHTML = `<span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.5s ease';
    setTimeout(() => toast.remove(), 500);
  }, 4000);
}

// Inline SVG Icon Library conforming to approved palette
const Icons = {
  shield: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>`,
  camera: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>`,
  clock: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`,
  check: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>`,
  alert: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`,
  book: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>`,
  user: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  logout: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`,
  chart: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>`,
  award: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>`,
  bell: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>`
};

function renderNavProfile(user) {
  const profileContainer = document.getElementById('user-profile-badge');
  if (profileContainer && user) {
    profileContainer.innerHTML = `
      <div class="avatar-circle">${user.fullName ? user.fullName.charAt(0) : 'U'}</div>
      <div class="user-badge-info">
        <h4>${user.fullName}</h4>
        <span>${user.role} &bull; ${user.departmentCode || user.registerOrEmployeeId || ''}</span>
      </div>
    `;
  }
}


/* ══════════════════════════════════════════
   auth.js — Login & Register page logic
══════════════════════════════════════════ */

// If already logged in, go straight to dashboard
if (getToken()) {
  window.location.href = 'dashboard.html';
}

// ── Tab switching ──────────────────────────
function showTab(tab) {
  const isLogin = tab === 'login';
  document.getElementById('loginForm').classList.toggle('hidden', !isLogin);
  document.getElementById('registerForm').classList.toggle('hidden', isLogin);
  document.getElementById('loginTab').classList.toggle('active', isLogin);
  document.getElementById('registerTab').classList.toggle('active', !isLogin);
  clearMessages();
}

function clearMessages() {
  ['loginMsg', 'registerMsg'].forEach(id => {
    const el = document.getElementById(id);
    el.className = 'form-msg';
    el.textContent = '';
  });
}

function showMsg(id, text, type = 'error') {
  const el = document.getElementById(id);
  el.className = `form-msg ${type}`;
  el.textContent = text;
}

function setLoading(btnId, loading) {
  const btn = document.getElementById(btnId);
  btn.disabled = loading;
  btn.querySelector('span').textContent = loading
    ? (btnId === 'loginBtn' ? 'Signing in…' : 'Creating account…')
    : (btnId === 'loginBtn' ? 'Sign In' : 'Create Account');
}

// ── Login ──────────────────────────────────
async function handleLogin(e) {
  e.preventDefault();
  const email    = document.getElementById('loginEmail').value.trim();
  const password = document.getElementById('loginPassword').value;

  clearMessages();
  setLoading('loginBtn', true);

  try {
    const token = await Auth.login(email, password);

    // Decode token to get role
    const payload = decodeToken(token);
    const role = payload?.role || 'USER';

    setToken(token);
    setEmail(email);
    setRole(role);

    showMsg('loginMsg', 'Success! Redirecting…', 'success');
    setTimeout(() => { window.location.href = 'dashboard.html'; }, 600);

  } catch (err) {
    showMsg('loginMsg', err.message || 'Login failed');
  } finally {
    setLoading('loginBtn', false);
  }
}

// ── Register ───────────────────────────────
async function handleRegister(e) {
  e.preventDefault();
  const name     = document.getElementById('regName').value.trim();
  const email    = document.getElementById('regEmail').value.trim();
  const password = document.getElementById('regPassword').value;

  if (password.length < 8) {
    showMsg('registerMsg', 'Password must be at least 8 characters');
    return;
  }

  clearMessages();
  setLoading('registerBtn', true);

  try {
    await Auth.register(name, email, password);
    showMsg('registerMsg', 'Account created! Signing you in…', 'success');

    // Auto-login after register
    setTimeout(async () => {
      try {
        const token = await Auth.login(email, password);
        const payload = decodeToken(token);
        setToken(token); setEmail(email); setRole(payload?.role || 'USER');
        window.location.href = 'dashboard.html';
      } catch {
        showMsg('registerMsg', 'Account created. Please sign in.', 'success');
        showTab('login');
      }
    }, 800);

  } catch (err) {
    showMsg('registerMsg', err.message || 'Registration failed');
  } finally {
    setLoading('registerBtn', false);
  }
}

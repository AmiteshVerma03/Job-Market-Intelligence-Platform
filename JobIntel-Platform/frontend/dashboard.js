/* ══════════════════════════════════════════
   dashboard.js — App page logic
   Uses addEventListener instead of inline
   onclick so file:// works correctly.
══════════════════════════════════════════ */

// ── Auth guard ─────────────────────────────
if (!getToken()) {
  window.location.href = 'index.html';
}

// ── State ──────────────────────────────────
let currentPage    = 'dashboard';
let jobsPage       = 0;
let jobsSize       = 12;
let jobsTotalPages = 0;
let searchType     = 'location';
let lastSearch     = '';
let pagesLoaded    = {};

// ══════════════════════════════════════════
//  INIT — runs when DOM is ready
// ══════════════════════════════════════════
document.addEventListener('DOMContentLoaded', () => {

  // Set user info in sidebar
  const email = getEmail() || 'user';
  const role  = getRole()  || 'USER';
  document.getElementById('userEmail').textContent  = email;
  document.getElementById('userAvatar').textContent = email.charAt(0).toUpperCase();

  if (role === 'ADMIN') {
    document.querySelectorAll('.admin-only').forEach(el => el.classList.remove('hidden'));
  }

  // ── Wire sidebar nav ──────────────────────
  document.querySelectorAll('.nav-item[data-page]').forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      navigate(link.dataset.page, link);
    });
  });

  // ── Wire logout buttons ───────────────────
  document.querySelectorAll('.btn-logout, .btn-logout-sm').forEach(btn => {
    btn.addEventListener('click', logout);
  });

  // ── Wire mobile menu ──────────────────────
  const menuBtn = document.querySelector('.menu-btn');
  if (menuBtn) menuBtn.addEventListener('click', toggleSidebar);

  // ── Wire search type tabs ─────────────────
  document.querySelectorAll('.stab').forEach(btn => {
    btn.addEventListener('click', () => setSearchType(btn.dataset.type, btn));
  });

  // ── Wire search button ────────────────────
  const searchBtn = document.getElementById('searchBtn');
  if (searchBtn) searchBtn.addEventListener('click', searchJobs);

  // ── Wire search input Enter key ───────────
  const searchInput = document.getElementById('jobSearchInput');
  if (searchInput) {
    searchInput.addEventListener('keydown', e => { if (e.key === 'Enter') searchJobs(); });
  }

  // ── Wire pagination ───────────────────────
  document.querySelectorAll('.prev-btn').forEach(b => b.addEventListener('click', () => changePage(-1)));
  document.querySelectorAll('.next-btn').forEach(b => b.addEventListener('click', () => changePage(1)));

  // ── Wire skills filters ───────────────────
  const applySkillsBtn = document.getElementById('applySkillsBtn');
  if (applySkillsBtn) applySkillsBtn.addEventListener('click', loadSkills);

  const skillLimit = document.getElementById('skillLimit');
  if (skillLimit) skillLimit.addEventListener('change', loadSkills);

  const skillLocationInput = document.getElementById('skillLocation');
  if (skillLocationInput) {
    skillLocationInput.addEventListener('keydown', e => { if (e.key === 'Enter') loadSkills(); });
  }

  // ── Wire salary lookup ────────────────────
  const salaryBtn = document.getElementById('salaryLookupBtn');
  if (salaryBtn) salaryBtn.addEventListener('click', lookupSalaryBySkill);

  const salaryInput = document.getElementById('salarySkillInput');
  if (salaryInput) {
    salaryInput.addEventListener('keydown', e => { if (e.key === 'Enter') lookupSalaryBySkill(); });
  }

  // ── Wire admin scraper button ─────────────
  const scraperBtn = document.getElementById('scraperBtn');
  if (scraperBtn) scraperBtn.addEventListener('click', runScraper);

  // ── Load dashboard data ───────────────────
  loadDashboard();
});

// ══════════════════════════════════════════
//  NAVIGATION
// ══════════════════════════════════════════
function navigate(page, linkEl) {
  document.querySelectorAll('.page').forEach(p => {
    p.classList.add('hidden');
    p.classList.remove('active');
  });
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

  const section = document.getElementById('page-' + page);
  if (section) { section.classList.remove('hidden'); section.classList.add('active'); }
  if (linkEl)  { linkEl.classList.add('active'); }

  currentPage = page;
  closeSidebar();

  // Lazy-load each page only once
  if (page === 'skills'    && !pagesLoaded.skills)    { loadSkills();           pagesLoaded.skills    = true; }
  if (page === 'companies' && !pagesLoaded.companies) { loadCompanies();        pagesLoaded.companies = true; }
  if (page === 'salary'    && !pagesLoaded.salary)    { loadSalaryByLocation(); pagesLoaded.salary    = true; }
  if (page === 'jobs'      && !pagesLoaded.jobs)      { loadAllJobs();          pagesLoaded.jobs      = true; }
}

function logout() {
  removeToken(); removeEmail(); removeRole();
  window.location.href = 'index.html';
}
function toggleSidebar() { document.getElementById('sidebar').classList.toggle('open'); }
function closeSidebar()  { document.getElementById('sidebar').classList.remove('open'); }

// ══════════════════════════════════════════
//  DASHBOARD
// ══════════════════════════════════════════
async function loadDashboard() {
  try {
    const jobsKpiEl = document.getElementById('kpiJobCount');
    if (jobsKpiEl) jobsKpiEl.textContent = '0';

    const [skills, companies, salaries, jobsCountData, jobsData] = await Promise.allSettled([
      Analytics.topSkills(5),
      Analytics.topCompanies(),
      Analytics.averageSalaryByLocation(),
      Jobs.count(),
      Jobs.getAll(0, 1)
    ]);

    if (jobsKpiEl) {
      if (jobsCountData.status === 'fulfilled') {
        const total = getTotalJobsCount(jobsCountData.value);
        jobsKpiEl.textContent = total.toLocaleString();
      } else if (jobsData.status === 'fulfilled') {
        const total = getTotalJobsCount(jobsData.value);
        jobsKpiEl.textContent = total.toLocaleString();
      } else {
        console.warn('Failed to load total jobs KPI:', {
          countError: jobsCountData.reason,
          pageError: jobsData.reason
        });
      }
    }

    if (skills.status === 'fulfilled') {
      const entries = Object.entries(skills.value);
      document.getElementById('kpiTopSkill').textContent = entries.length > 0 ? entries[0][0] : 'No data';
      if (entries.length > 0) renderBarChart('dashSkillsChart', skills.value, 5);
      else document.getElementById('dashSkillsChart').innerHTML = emptyMsg('No data yet — run the scraper first');
    } else {
      document.getElementById('dashSkillsChart').innerHTML = emptyMsg('Could not load skills');
    }

    if (companies.status === 'fulfilled') {
      const entries = Object.entries(companies.value);
      document.getElementById('kpiTopCompany').textContent = entries.length > 0 ? entries[0][0] : 'No data';
      if (entries.length > 0) renderBarChart('dashCompaniesChart', companies.value, 5);
      else document.getElementById('dashCompaniesChart').innerHTML = emptyMsg('No data yet — run the scraper first');
    } else {
      document.getElementById('dashCompaniesChart').innerHTML = emptyMsg('Could not load companies');
    }

    if (salaries.status === 'fulfilled') {
      const entries = Object.entries(salaries.value);
      if (entries.length > 0) {
        const avg = entries.reduce((s, [, v]) => s + v, 0) / entries.length;
        document.getElementById('kpiAvgSalary').textContent = avg > 0 ? '$' + Math.round(avg).toLocaleString() : 'N/A';
        renderSalaryChart('dashSalaryChart', salaries.value, 8);
      } else {
        document.getElementById('kpiAvgSalary').textContent = 'N/A';
        document.getElementById('dashSalaryChart').innerHTML = emptyMsg('No salary data yet');
      }
    }

  } catch (err) {
    console.error('Dashboard load error:', err);
  }
}

function getTotalJobsCount(data) {
  if (!data) return 0;

  const totalElements = toFiniteNumber(data.totalElements);
  if (totalElements !== null) return totalElements;

  const total = toFiniteNumber(data.total);
  if (total !== null) return total;

  const count = toFiniteNumber(data.count);
  if (count !== null) return count;

  if (data.page) {
    const pageTotalElements = toFiniteNumber(data.page.totalElements);
    if (pageTotalElements !== null) return pageTotalElements;

    const pageTotalElementsCount = toFiniteNumber(data.page.totalElementsCount);
    if (pageTotalElementsCount !== null) return pageTotalElementsCount;
  }

  if (Array.isArray(data.content)) return data.content.length;
  if (Array.isArray(data)) return data.length;

  return 0;
}

function toFiniteNumber(value) {
  const num = (typeof value === 'string') ? Number(value) : value;
  return Number.isFinite(num) ? num : null;
}

// ══════════════════════════════════════════
//  CHART RENDERERS
// ══════════════════════════════════════════
function renderBarChart(containerId, data, maxItems = 10) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const entries = Object.entries(data).slice(0, maxItems);
  if (entries.length === 0) { container.innerHTML = emptyMsg('No data available'); return; }
  const maxVal = Math.max(...entries.map(([, v]) => Number(v)));
  container.innerHTML = entries.map(([label, val], i) => {
    const pct = maxVal > 0 ? (Number(val) / maxVal * 100) : 0;
    return `<div class="bar-row" style="animation-delay:${i * 0.06}s">
      <div class="bar-label" title="${escHtml(label)}">${escHtml(label)}</div>
      <div class="bar-track"><div class="bar-fill" style="width:0" data-width="${pct}%"></div></div>
      <div class="bar-val">${Number(val).toLocaleString()}</div>
    </div>`;
  }).join('');
  requestAnimationFrame(() => {
    container.querySelectorAll('.bar-fill').forEach(el => { el.style.width = el.dataset.width; });
  });
}

function renderSalaryChart(containerId, data, maxItems = 10) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const entries = Object.entries(data).sort(([, a], [, b]) => b - a).slice(0, maxItems);
  if (entries.length === 0) { container.innerHTML = emptyMsg('No salary data available'); return; }
  const maxVal = Math.max(...entries.map(([, v]) => Number(v)));
  container.innerHTML = entries.map(([loc, sal], i) => {
    const pct  = maxVal > 0 ? (Number(sal) / maxVal * 100) : 0;
    const fmtd = sal > 0 ? '$' + Math.round(sal).toLocaleString() : 'N/A';
    return `<div class="salary-row" style="animation-delay:${i * 0.06}s">
      <div class="bar-label" title="${escHtml(loc)}">${escHtml(loc)}</div>
      <div class="bar-track"><div class="bar-fill salary-bar-fill" style="width:0" data-width="${pct}%"></div></div>
      <div class="bar-val">${fmtd}</div>
    </div>`;
  }).join('');
  requestAnimationFrame(() => {
    container.querySelectorAll('.bar-fill').forEach(el => { el.style.width = el.dataset.width; });
  });
}

// ══════════════════════════════════════════
//  JOBS PAGE
// ══════════════════════════════════════════
function setSearchType(type, btn) {
  searchType = type;
  document.querySelectorAll('.stab').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  const input = document.getElementById('jobSearchInput');
  if (!input) return;
  if (type === 'all') {
    input.placeholder = 'Shows all jobs (paginated)';
    input.value = '';
    input.disabled = true;
  } else {
    input.disabled = false;
    input.placeholder = { location: 'e.g. New York, Remote…', company: 'e.g. Google, Amazon…', skill: 'e.g. Python, React…' }[type];
    input.focus();
  }
}

async function searchJobs() {
  const query = (document.getElementById('jobSearchInput').value || '').trim();
  jobsPage = 0;
  if (searchType !== 'all' && !query) { renderJobsError('Please enter a search term'); return; }
  lastSearch = query;
  renderJobsLoading();
  try {
    if      (searchType === 'all')      await loadAllJobs();
    else if (searchType === 'location') renderJobsList(await Jobs.searchByLocation(query));
    else if (searchType === 'company')  renderJobsList(await Jobs.searchByCompany(query));
    else if (searchType === 'skill')    renderJobsList(await Jobs.searchBySkill(query));
  } catch (err) { renderJobsError(err.message); }
}

async function loadAllJobs() {
  renderJobsLoading();
  try {
    const data = await Jobs.getAll(jobsPage, jobsSize);
    jobsTotalPages = data.totalPages ?? 1;
    renderJobsList(data.content ?? []);
    updatePagination();
  } catch (err) { renderJobsError(err.message); }
}

async function changePage(delta) {
  const next = jobsPage + delta;
  if (next < 0 || next >= jobsTotalPages) return;
  jobsPage = next;
  await loadAllJobs();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function updatePagination() {
  const show = jobsTotalPages > 1;
  ['paginationBar', 'paginationBarBottom'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.style.display = show ? 'flex' : 'none';
  });
  const txt = `Page ${jobsPage + 1} of ${jobsTotalPages}`;
  ['pageInfo', 'pageInfoBottom'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.textContent = txt;
  });
}

function renderJobsList(jobs) {
  const grid = document.getElementById('jobsGrid');
  if (!jobs || jobs.length === 0) { grid.innerHTML = '<div class="empty-state">No jobs found for this search</div>'; return; }
  grid.innerHTML = jobs.map((job, i) => {
    const skills = job.skills && job.skills.length > 0
      ? `<div class="job-skills">${job.skills.slice(0, 5).map(s => `<span class="skill-tag">${escHtml(s.name || s)}</span>`).join('')}${job.skills.length > 5 ? `<span class="skill-tag">+${job.skills.length - 5}</span>` : ''}</div>` : '';
    const salary = job.salary ? `<div class="job-salary">$${job.salary.toLocaleString()}</div>` : '';
    const url    = job.url    ? `<div class="job-url"><a href="${job.url}" target="_blank" rel="noopener">↗ View Job</a></div>` : '';
    return `<div class="job-card" style="animation-delay:${(i % 12) * 0.04}s">
      <div class="job-title">${escHtml(job.title || 'Untitled')}</div>
      <div class="job-meta">
        ${job.company  ? `<div class="job-meta-item"><span>🏢</span> ${escHtml(job.company)}</div>`  : ''}
        ${job.location ? `<div class="job-meta-item"><span>📍</span> ${escHtml(job.location)}</div>` : ''}
      </div>
      ${salary}${skills}${url}
    </div>`;
  }).join('');
}

function renderJobsLoading() { document.getElementById('jobsGrid').innerHTML = '<div class="loading-pulse">Loading jobs…</div>'; }
function renderJobsError(msg){ document.getElementById('jobsGrid').innerHTML = `<div class="empty-state">⚠ ${escHtml(msg)}</div>`; }

// ══════════════════════════════════════════
//  SKILLS PAGE
// ══════════════════════════════════════════
async function loadSkills() {
  const location  = (document.getElementById('skillLocation').value || '').trim();
  const limitEl   = document.getElementById('skillLimit');
  const limit     = limitEl ? parseInt(limitEl.value) : 10;
  const container = document.getElementById('skillsContainer');
  container.innerHTML = '<div class="loading-pulse">Loading skills…</div>';
  try {
    const data    = location ? await Analytics.topSkillsByLocation(location, limit) : await Analytics.topSkills(limit);
    const entries = Object.entries(data);
    if (entries.length === 0) { container.innerHTML = emptyMsg('No skills data yet. Run the scraper first.'); return; }
    const maxVal  = Math.max(...entries.map(([, v]) => Number(v)));
    container.innerHTML = entries.map(([name, count], i) => {
      const pct   = maxVal > 0 ? (Number(count) / maxVal * 100) : 0;
      const isTop = i < 3;
      return `<div class="skill-card" style="animation-delay:${i * 0.04}s">
        <div class="skill-rank ${isTop ? 'top3' : ''}">#${i + 1}</div>
        <div class="skill-info">
          <div class="skill-name">${escHtml(name)}</div>
          <div class="skill-bar-wrap"><div class="skill-bar-inner" style="width:0" data-width="${pct}%"></div></div>
        </div>
        <div class="skill-count">${Number(count).toLocaleString()}</div>
      </div>`;
    }).join('');
    requestAnimationFrame(() => {
      container.querySelectorAll('.skill-bar-inner').forEach(el => { el.style.width = el.dataset.width; });
    });
  } catch (err) { container.innerHTML = emptyMsg('Error: ' + err.message); }
}

// ══════════════════════════════════════════
//  COMPANIES PAGE
// ══════════════════════════════════════════
async function loadCompanies() {
  const container = document.getElementById('companiesContainer');
  container.innerHTML = '<div class="loading-pulse">Loading companies…</div>';
  try {
    const data    = await Analytics.topCompanies();
    const entries = Object.entries(data);
    if (entries.length === 0) { container.innerHTML = emptyMsg('No company data yet. Run the scraper first.'); return; }
    container.innerHTML = entries.map(([name, count], i) => {
      const isTop = i < 3;
      return `<div class="company-card" style="animation-delay:${i * 0.03}s">
        <div class="company-rank ${isTop ? 'top3' : ''}">#${i + 1}</div>
        <div class="company-info">
          <div class="company-name" title="${escHtml(name)}">${escHtml(name)}</div>
          <div class="company-jobs">${Number(count).toLocaleString()} job${count != 1 ? 's' : ''}</div>
        </div>
        <div class="company-badge">${Number(count).toLocaleString()}</div>
      </div>`;
    }).join('');
  } catch (err) { container.innerHTML = emptyMsg('Error: ' + err.message); }
}

// ══════════════════════════════════════════
//  SALARY PAGE
// ══════════════════════════════════════════
async function lookupSalaryBySkill() {
  const skill  = (document.getElementById('salarySkillInput').value || '').trim();
  if (!skill) return;
  const result = document.getElementById('salarySkillResult');
  result.innerHTML = '<div class="loading-pulse">Looking up…</div>';
  result.classList.add('visible');
  try {
    const data = await Analytics.salaryBySkill(skill);
    const avg  = data.averageSalary;
    result.innerHTML = `
      <div class="result-skill">${escHtml(data.skill)}</div>
      <div class="result-value">${avg > 0 ? '$' + Math.round(avg).toLocaleString() : 'No data'}</div>
      <div class="result-sub">average salary</div>`;
  } catch (err) {
    result.innerHTML = `<div class="result-sub" style="color:var(--danger)">${escHtml(err.message)}</div>`;
  }
}

async function loadSalaryByLocation() {
  const container = document.getElementById('salaryLocationChart');
  container.innerHTML = '<div class="loading-pulse">Loading…</div>';
  try {
    const data = await Analytics.averageSalaryByLocation();
    if (Object.keys(data).length === 0) { container.innerHTML = emptyMsg('No salary data yet.'); return; }
    renderSalaryChart('salaryLocationChart', data, 15);
  } catch (err) { container.innerHTML = emptyMsg('Error: ' + err.message); }
}

// ══════════════════════════════════════════
//  ADMIN
// ══════════════════════════════════════════
async function runScraper() {
  const btn = document.getElementById('scraperBtn');
  const msg = document.getElementById('scraperMsg');
  btn.disabled    = true;
  btn.textContent = '⟳ Running…';
  msg.className   = 'form-msg success';
  msg.style.display = 'block';
  msg.textContent = 'Scraper started — this takes several minutes.';
  try {
    const res = await Admin.runScraper();
    msg.textContent = '✓ ' + (res.message || 'Scraper completed');
    pagesLoaded = {};
    loadDashboard();
  } catch (err) {
    msg.className   = 'form-msg error';
    msg.textContent = '✗ ' + (err.message || 'Scraper failed');
  } finally {
    btn.disabled    = false;
    btn.textContent = '▶ Run Scraper Now';
  }
}

// ══════════════════════════════════════════
//  UTILITIES
// ══════════════════════════════════════════
function escHtml(str) {
  if (!str) return '';
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
function emptyMsg(msg) {
  return `<div class="empty-state" style="grid-column:1/-1">${escHtml(msg)}</div>`;
}

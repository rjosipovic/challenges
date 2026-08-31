import { api } from '../api.js';
  import { formatDate, formatCurrency, escapeHtml } from '../utils.js';
  
  export async function renderFinance(container) {
      const now = new Date();
      let year = now.getFullYear();
      let month = now.getMonth() + 1;
  
      container.innerHTML = `
          <h1 class="mb-2">Financije</h1>
  
          <div class="card">
              <h2>Mjesečni prihod</h2>
              <div style="display: flex; gap: 1rem; align-items: end; margin-bottom: 1rem;">
                  <div class="form-group" style="margin-bottom: 0;">
                      <label for="fin-year">Godina</label>
                      <input type="number" id="fin-year" value="${year}" min="2020" max="2030" style="width: 100px;" />
                  </div>
                  <div class="form-group" style="margin-bottom: 0;">
                      <label for="fin-month">Mjesec</label>
                      <input type="number" id="fin-month" value="${month}" min="1" max="12" style="width: 80px;" />
                  </div>
                  <button id="load-revenue-btn" class="btn btn-primary">Prikaži</button>
              </div>
              <div id="revenue-data"></div>
          </div>
  
          <div class="card">
              <h2>Čekaju potvrdu uplate</h2>
              <div id="pending-payments"></div>
          </div>
      `;
  
      document.getElementById('load-revenue-btn').addEventListener('click', () => {
          year = parseInt(document.getElementById('fin-year').value);
          month = parseInt(document.getElementById('fin-month').value);
          loadRevenue(year, month);
      });
  
      await Promise.all([
          loadRevenue(year, month),
          loadPendingPayments()
      ]);
  }
  
  async function loadRevenue(year, month) {
      const revenueEl = document.getElementById('revenue-data');
  
      try {
          const data = await api.get(`/dashboard/finance/monthly?year=${year}&month=${month}`);
  
          revenueEl.innerHTML = `
              <table>
                  <tbody>
                      <tr><td><strong>Ukupni prihod</strong></td><td>${formatCurrency(data.totalRevenue)}</td></tr>
                      <tr><td>Stripe</td><td>${formatCurrency(data.stripePayments)}</td></tr>
                      <tr><td>Bankovni prijenos</td><td>${formatCurrency(data.bankTransferPayments)}</td></tr>
                      <tr><td>Gotovina / Ostalo</td><td>${formatCurrency(data.cashPayments)}</td></tr>
                      <tr><td><strong>Održanih termina</strong></td><td>${data.completedAppointments}</td></tr>
                      <tr><td><strong>Naplativi sati</strong></td><td>${data.billableHours}h</td></tr>
                  </tbody>
              </table>
          `;
      } catch (err) {
          revenueEl.innerHTML = `<p style="color: #dc2626;">Greška pri učitavanju</p>`;
      }
  }
  
  async function loadPendingPayments() {
      const pendingEl = document.getElementById('pending-payments');
  
      try {
          const payments = await api.get('/dashboard/finance/pending');
  
          if (!payments || payments.length === 0) {
              pendingEl.innerHTML = '<p class="text-muted">Nema čekajućih uplata</p>';
              return;
          }
  
          pendingEl.innerHTML = `
              <table>
                  <thead><tr><th>Student</th><th>Iznos</th><th>Datum</th><th>Akcija</th></tr></thead>
                  <tbody>
                      ${payments.map(p => `
                          <tr>
                              <td>${escapeHtml(p.studentName)}</td>
                              <td>${formatCurrency(p.amount)}</td>
                              <td>${formatDate(p.createdAt?.split('T')[0])}</td>
                              <td><button class="btn btn-success btn-sm" data-confirm="${p.appointmentId}">Potvrdi</button></td>
                          </tr>
                      `).join('')}
                  </tbody>
              </table>
          `;
  
          pendingEl.querySelectorAll('[data-confirm]').forEach(btn => {
              btn.addEventListener('click', async () => {
                  try {
                      await api.post(`/dashboard/finance/confirm-bank-transfer/${btn.dataset.confirm}`);
                      await loadPendingPayments();
                      // Refresh revenue too
                      const year = parseInt(document.getElementById('fin-year').value);
                      const month = parseInt(document.getElementById('fin-month').value);
                      await loadRevenue(year, month);
                  } catch (err) {
                      alert(err.reason || 'Greška pri potvrdi');
                  }
              });
          });
      } catch (err) {
          pendingEl.innerHTML = `<p style="color: #dc2626;">Greška pri učitavanju</p>`;
      }
  }
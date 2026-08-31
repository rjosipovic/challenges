import { api } from './api.js';
  import { formatDate, formatTime, escapeHtml } from './utils.js';
  
  async function init() {
      const container = document.getElementById('page-container');
      const params = new URLSearchParams(window.location.search);
      const token = params.get('token');
  
      if (!token) {
          container.innerHTML = '<p style="color: #dc2626;">Nevažeća poveznica — token nije pronađen.</p>';
          return;
      }
  
      container.innerHTML = '<p>Učitavanje...</p>';
  
      try {
          const details = await api.get(`/storefront/appointments?token=${token}`);
  
          container.innerHTML = `
              <div class="card">
                  <h2>Vaš termin</h2>
                  <table>
                      <tbody>
                          <tr><td><strong>Student</strong></td><td>${escapeHtml(details.studentName)}</td></tr>
                          <tr><td><strong>Usluga</strong></td><td>${escapeHtml(details.serviceCategoryName)}</td></tr>
                          <tr><td><strong>Datum</strong></td><td>${formatDate(details.date)}</td></tr>
                          <tr><td><strong>Vrijeme</strong></td><td>${formatTime(details.startTime)}</td></tr>
                      </tbody>
                  </table>
  
                  ${details.deadlineMissed ? `
                      <div class="mt-2" style="background: #fef3c7; padding: 1rem; border-radius: 4px;">
                          <p>⚠️ Rok za otkazivanje/premještanje je prošao. Kontaktirajte instruktora.</p>
                      </div>
                  ` : `
                      <div class="mt-2" style="display: flex; gap: 1rem;">
                          <button id="cancel-btn" class="btn btn-danger">Otkaži termin</button>
                          <button id="reschedule-btn" class="btn btn-primary">Premjesti termin</button>
                      </div>
                  `}
  
                  <p id="manage-error" class="text-muted mt-1 hidden" style="color: #dc2626;"></p>
                  <div id="manage-result" class="hidden"></div>
              </div>
          `;
  
          if (!details.deadlineMissed) {
              document.getElementById('cancel-btn').addEventListener('click', () => handleCancel(token));
              document.getElementById('reschedule-btn').addEventListener('click', () => handleReschedule(token));
          }
  
      } catch (err) {
          container.innerHTML = `
              <div class="card">
                  <p style="color: #dc2626;">${escapeHtml(err.reason || err.message || 'Poveznica je nevažeća ili je istekla.')}</p>
              </div>
          `;
      }
  }
  
  async function handleCancel(token) {
      const errorEl = document.getElementById('manage-error');
      const resultEl = document.getElementById('manage-result');
      const cancelBtn = document.getElementById('cancel-btn');
      const rescheduleBtn = document.getElementById('reschedule-btn');
  
      cancelBtn.disabled = true;
      cancelBtn.textContent = 'Otkazivanje...';
  
      try {
          await api.post('/storefront/appointments/cancel', { token });
  
          cancelBtn.classList.add('hidden');
          rescheduleBtn.classList.add('hidden');
          resultEl.classList.remove('hidden');
          resultEl.innerHTML = `
              <div class="success-box mt-2">
                  <h2>✅ Termin otkazan</h2>
                  <p class="mt-1">Vaš termin je uspješno otkazan.</p>
              </div>
          `;
      } catch (err) {
          errorEl.textContent = err.reason || 'Greška pri otkazivanju';
          errorEl.classList.remove('hidden');
          cancelBtn.disabled = false;
          cancelBtn.textContent = 'Otkaži termin';
      }
  }
  
  async function handleReschedule(token) {
      const errorEl = document.getElementById('manage-error');
      const resultEl = document.getElementById('manage-result');
      const cancelBtn = document.getElementById('cancel-btn');
      const rescheduleBtn = document.getElementById('reschedule-btn');
  
      rescheduleBtn.disabled = true;
      rescheduleBtn.textContent = 'Obrada...';
  
      try {
          const result = await api.post('/storefront/appointments/reschedule', { token });
  
          cancelBtn.classList.add('hidden');
          rescheduleBtn.classList.add('hidden');
          resultEl.classList.remove('hidden');
          resultEl.innerHTML = `
              <div class="success-box mt-2">
                  <h2>🔄 Premještanje termina</h2>
                  <p class="mt-1">Vaš prethodni termin je otkazan. Odaberite novi termin:</p>
                  <a href="index.html?rescheduleToken=${result.rescheduleToken}" class="btn btn-primary mt-2" style="display: inline-block; text-decoration: none;">Odaberi novi termin</a>
              </div>
          `;
      } catch (err) {
          errorEl.textContent = err.reason || 'Greška pri premještanju';
          errorEl.classList.remove('hidden');
          rescheduleBtn.disabled = false;
          rescheduleBtn.textContent = 'Premjesti termin';
      }
  }
  
  init();

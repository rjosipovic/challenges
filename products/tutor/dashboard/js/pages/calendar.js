import { api } from '../api.js';
  import { formatDate, formatTime, escapeHtml } from '../utils.js';
  
  let currentWeekStart = getMonday(new Date());
  let bookingSlotId = null;
  let cancelAppointmentId = null;
  
  export async function renderCalendar(container) {
      container.innerHTML = `
          <h1 class="mb-2">Kalendar</h1>
  
          <div class="card">
              <h2>Novi termini</h2>
              <div style="display: flex; gap: 1rem; align-items: end; flex-wrap: wrap;">
                  <div class="form-group" style="margin-bottom: 0;">
                      <label for="slot-date">Datum</label>
                      <input type="date" id="slot-date" />
                  </div>
                  <div class="form-group" style="margin-bottom: 0;">
                      <label for="slot-time">Početak</label>
                      <input type="time" id="slot-time" step="3600" />
                  </div>
                  <button id="add-slot-btn" class="btn btn-primary">Dodaj</button>
              </div>
              <div id="pending-slots" class="mt-2"></div>
              <div id="slot-actions" class="mt-2 hidden" style="display: flex; gap: 0.5rem;">
                  <button id="create-slots-btn" class="btn btn-primary">Spremi kao nacrt</button>
              </div>
          </div>
  
          <div class="card">
              <h2>Tjedni pregled</h2>
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                  <button id="prev-week" class="btn btn-primary">← Prethodni</button>
                  <span id="week-label"></span>
                  <button id="next-week" class="btn btn-primary">Sljedeći →</button>
              </div>
              <div id="week-grid"></div>
          </div>

          <div id="booking-modal" class="card hidden" style="position:fixed;top:50%;left:50%;transform:translate(-50%, -50%);z-index:1000;width:400px;box-shadow:0 4px 20px rgba(0,0,0,0.3)">
            <h2>Direktno zauzimanje</h2>
            <div class="form-group">
                <label for="book-student">Student</label>
                <select id="book-student"></select>
            </div>
            <div class="form-group">
                <label for="book-service">Usluga</label>
                <select id="book-service"></select>
            </div>
            <div style="display:flex;gap:0.5rem;">
                <button id="confirm-booking" class="btn btn-primary">Potvrdi</button>
                <button id="cancel-booking" class="btn">Odustani</button>
            </div>
          </div>
          <div id="cancel-modal" class="card hidden" style="position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);z-index:1000;width:400px;box-shadow:0 4px 20px rgba(0,0,0,0.3)">
            <h2>Otkazivanje termina</h2>
            <div class="form-group">
                <label for="cancel-reason">Razlog otkazivanja</label>
                <textarea id="cancel-reason" rows="3" style="width:100%"></textarea>
            </div>
            <div style="display:flex;gap:0.5rem;">
                <button id="confirm-cancel" class="btn btn-danger">Otkaži termin</button>
                <button id="dismiss-cancel" class="btn">Odustani</button>
            </div>
          </div>
          <div id="booking-overlay" class="hidden" style="position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,0.3);z-index:999;"></div>
      `;
  
      const pendingSlots = [];
  
      // Add slot to pending list
      document.getElementById('add-slot-btn').addEventListener('click', () => {
          const date = document.getElementById('slot-date').value;
          const time = document.getElementById('slot-time').value;
          if (!date || !time) return;
  
          pendingSlots.push({ date, startTime: time });
          renderPendingSlots(pendingSlots);
      });
  
      // Create slots (save as draft)
      document.getElementById('create-slots-btn').addEventListener('click', async () => {
          if (pendingSlots.length === 0) return;
  
          try {
              await api.post('/dashboard/slots', { slots: pendingSlots });
              pendingSlots.length = 0;
              renderPendingSlots(pendingSlots);
              loadWeekGrid();
          } catch (err) {
              alert(err.reason || 'Greška pri stvaranju termina');
          }
      });
  
      // Week navigation
      document.getElementById('prev-week').addEventListener('click', () => {
          currentWeekStart.setDate(currentWeekStart.getDate() - 7);
          loadWeekGrid();
      });
  
      document.getElementById('next-week').addEventListener('click', () => {
          currentWeekStart.setDate(currentWeekStart.getDate() + 7);
          loadWeekGrid();
      });

      // Direct booking
      document.getElementById('confirm-booking').addEventListener('click', async () => {
        const studentId = document.getElementById('book-student').value;
        const serviceCategoryId = document.getElementById('book-service').value;

        if (!studentId || !serviceCategoryId) return;

        try {
            await api.post('/dashboard/bookings/direct', {
                timeSlotId: bookingSlotId,
                studentId,
                serviceCategoryId
            });
            closeBookingModal();
            loadWeekGrid();
        } catch (err) {
            alert(err.reason || 'Greška pri zauzimanju');
        }
      });
      
      document.getElementById('cancel-booking').addEventListener('click', closeBookingModal);
      document.getElementById('booking-overlay').addEventListener('click', () => {
        closeBookingModal();
        closeCancelModal();
      });

      document.getElementById('confirm-cancel').addEventListener('click', async () => {
        const reason = document.getElementById('cancel-reason').value.trim();
        if (!reason) {
            alert('Razlog je obavezan');
            return;
        }
        try {
            await api.post(`/dashboard/appointments/${cancelAppointmentId}/cancel`, { reason });
            closeCancelModal();
            loadWeekGrid();
        } catch(err) {
            alert(err.reason || 'Greška pri otkazivanju')
        }
      });
      document.getElementById('dismiss-cancel').addEventListener('click', closeCancelModal);
  
      loadWeekGrid();
  }
  
  function renderPendingSlots(slots) {
      const container = document.getElementById('pending-slots');
      const actions = document.getElementById('slot-actions');
  
      if (slots.length === 0) {
          container.innerHTML = '';
          actions.classList.add('hidden');
          return;
      }
  
      actions.classList.remove('hidden');
      container.innerHTML = `
          <table>
              <thead><tr><th>Datum</th><th>Vrijeme</th><th></th></tr></thead>
              <tbody>
                  ${slots.map((s, i) => `
                      <tr>
                          <td>${formatDate(s.date)}</td>
                          <td>${formatTime(s.startTime)}</td>
                          <td><button class="btn btn-danger btn-sm" data-remove="${i}">✕</button></td>
                      </tr>
                  `).join('')}
              </tbody>
          </table>
      `;
  
      container.querySelectorAll('[data-remove]').forEach(btn => {
          btn.addEventListener('click', () => {
              slots.splice(parseInt(btn.dataset.remove), 1);
              renderPendingSlots(slots);
          });
      });
  }
  
  async function loadWeekGrid() {
      const grid = document.getElementById('week-grid');
      const label = document.getElementById('week-label');
  
      const from = toISODate(currentWeekStart);
      const to = toISODate(addDays(currentWeekStart, 6));
  
      label.textContent = `${formatDate(from)} – ${formatDate(to)}`;
  
      try {
          const slots = await api.get(`/dashboard/slots?from=${from}&to=${to}`);
          renderWeekGrid(grid, slots);
      } catch (err) {
          grid.innerHTML = `<p style="color: #dc2626;">Greška pri učitavanju</p>`;
      }
  }
  
  function renderWeekGrid(grid, entries) {
      if (!entries || entries.length === 0) {
          grid.innerHTML = '<p class="text-muted">Nema termina ovaj tjedan</p>';
          return;
      }
  
      const stateLabels = {
          'DRAFT': '📝 Nacrt',
          'AVAILABLE': '✅ Dostupan',
          'RESERVED': '⏳ Rezerviran',
          'BOOKED': '📌 Zauzet',
          'PRE_BOOKED': '👤 Direktan'
      };
  
      const stateColors = {
          'DRAFT': '#f3f4f6',
          'AVAILABLE': '#dcfce7',
          'RESERVED': '#fef3c7',
          'BOOKED': '#dbeafe',
          'PRE_BOOKED': '#ede9fe'
      };
  
      const rows = entries.map(entry => {
          const slot = entry.slot;
          return `
          <tr style="background: ${stateColors[slot.state] || 'white'}">
              <td>${formatDate(slot.date)}</td>
              <td>${formatTime(slot.startTime)} – ${formatTime(slot.endTime)}</td>
              <td>${stateLabels[slot.state] || slot.state}</td>
              <td>${renderSlotActions(slot, entry.appointment)}</td>
          </tr>
      `;
      }).join('');
  
      grid.innerHTML = `
          <table>
              <thead><tr><th>Datum</th><th>Vrijeme</th><th>Status</th><th>Akcije</th></tr></thead>
              <tbody>${rows}</tbody>
          </table>
      `;
  
      attachSlotActionHandlers(grid);
  }
  
  function renderSlotActions(slot, appointment) {
      const actions = [];
  
      if (slot.state === 'DRAFT') {
          actions.push(`<button class="btn btn-success btn-sm" data-publish="${slot.id}">Objavi</button>`);
          actions.push(`<button class="btn btn-danger btn-sm" data-delete="${slot.id}">Obriši</button>`);
      }
  
      if (slot.state === 'AVAILABLE') {
          actions.push(`<button class="btn btn-primary btn-sm" data-direct-book="${slot.id}">Zauzmi</button>`);
          actions.push(`<button class="btn btn-sm" style="background:#6b7280;color:white;" data-withdraw="${slot.id}">Povuci</button>`);
      }

      if (appointment) {
        actions.push(`<button class="btn btn-danger btn-sm" data-cancel-appointment="${appointment.id}">Otkaži</button>`);
      }
  
      return actions.join(' ');
  }
  
  function attachSlotActionHandlers(grid) {
      grid.querySelectorAll('[data-publish]').forEach(btn => {
          btn.addEventListener('click', async () => {
              try {
                  await api.patch('/dashboard/slots/publish', { slotIds: [btn.dataset.publish] });
                  loadWeekGrid();
              } catch (err) {
                  alert(err.reason || 'Greška');
              }
          });
      });
  
      grid.querySelectorAll('[data-withdraw]').forEach(btn => {
          btn.addEventListener('click', async () => {
              try {
                  await api.patch('/dashboard/slots/withdraw', { slotIds: [btn.dataset.withdraw] });
                  loadWeekGrid();
              } catch (err) {
                  alert(err.reason || 'Greška');
              }
          });
      });
  
      grid.querySelectorAll('[data-delete]').forEach(btn => {
          btn.addEventListener('click', async () => {
              try {
                  await api.delete('/dashboard/slots', { slotIds: [btn.dataset.delete] });
                  loadWeekGrid();
              } catch (err) {
                  alert(err.reason || 'Greška');
              }
          });
      });

      grid.querySelectorAll('[data-direct-book]').forEach(btn => {
        btn.addEventListener('click', () => openBookingModal(btn.dataset.directBook))
      });

      grid.querySelectorAll('[data-cancel-appointment]').forEach(btn => {
        btn.addEventListener('click', () => openCancelModal(btn.dataset.cancelAppointment));
      });
  }

  async function openBookingModal(slotId) {
    bookingSlotId = slotId;
    const modal = document.getElementById('booking-modal');
    const overlay = document.getElementById('booking-overlay');

    try {
        const [students, services] = await Promise.all([
            api.get('/dashboard/students'),
            api.get('/storefront/services')
        ]);

        const studentSelect = document.getElementById('book-student');
        studentSelect.innerHTML = students.map(s =>
            `<option value="${s.id}">${escapeHtml(s.name)}</option>`
        ).join('');

        const serviceSelect = document.getElementById('book-service');
        serviceSelect.innerHTML = services.map(s =>
            `<option value="${s.id}">${escapeHtml(s.name)} (${s.price} €)</option>`
        ).join('');

        modal.classList.remove('hidden');
        overlay.classList.remove('hidden')
        
    } catch (err) {
        alert('Greška pri učitavanju podataka');
    }    
  }

  function closeBookingModal() {
    document.getElementById('booking-modal').classList.add('hidden');
    document.getElementById('booking-overlay').classList.add('hidden');
    bookingSlotId = null;
  }

  function openCancelModal(appointmentId) {
    cancelAppointmentId = appointmentId;
    document.getElementById('cancel-reason').value = '';
    document.getElementById('cancel-modal').classList.remove('hidden');
    document.getElementById('booking-overlay').classList.remove('hidden');
  }

  function closeCancelModal() {
    document.getElementById('cancel-modal').classList.add('hidden');
    document.getElementById('booking-overlay').classList.add('hidden');
    cancelAppointmentId = null;
  }
  
  // --- Helpers ---
  
  function getMonday(date) {
      const d = new Date(date);
      const day = d.getDay();
      const diff = d.getDate() - day + (day === 0 ? -6 : 1);
      d.setDate(diff);
      d.setHours(0, 0, 0, 0);
      return d;
  }
  
  function addDays(date, days) {
      const d = new Date(date);
      d.setDate(d.getDate() + days);
      return d;
  }
  
  function toISODate(date) {
      return date.toISOString().split('T')[0];
  }

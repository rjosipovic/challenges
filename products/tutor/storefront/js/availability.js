import { api } from './api.js';
  import { formatDate, formatTime, escapeHtml } from './utils.js';
  import { navigateTo } from './app.js';
  
  export async function renderAvailability(container, data = {}) {
      const today = new Date();
      const from = toISODate(today);
      const to = toISODate(addDays(today, 13)); // 14 rolling days
  
      container.innerHTML = '<p>Učitavanje termina...</p>';
  
      try {
          const slots = await api.get(`/storefront/availability?from=${from}&to=${to}`);

          const isReschedule = !!data.rescheduleToken;
  
          container.innerHTML = `
              <div class="steps">
                  <div class="step ${isReschedule ? 'done' : 'done'}"><span class="step-number">✓</span> Usluga</div>
                  <div class="step active"><span class="step-number">2</span> Termin</div>
                  <div class="step"><span class="step-number">3</span> Rezervacija</div>
              </div>
  
              <div class="card">
                  ${isReschedule ? '<p class="mb-2" style="background:#dcfce7;padding:0.75rem;border-radius:4px;">🔄 Premještanje termina — odaberite  novi termin (plaćanje nije potrebno)</p>' : 
                  `<p class="text-muted mb-2">Odabrana usluga: <strong>${escapeHtml(data.serviceName || '')}</strong></p>`}
                  <h2 class="mb-2">Odaberite termin</h2>
  
                  ${slots.length === 0 ? '<p class="text-muted">Nema dostupnih termina u sljedećih 14 dana</p>' : `
                      <div class="slots-grid" id="slots-grid">
                          ${slots.map(slot => `
                              <div class="slot-card" data-slot-id="${slot.id}">
                                  <div class="slot-date">${formatDate(slot.date)}</div>
                                  <div class="slot-time">${formatTime(slot.startTime)}</div>
                              </div>
                          `).join('')}
                      </div>
                  `}
  
                  ${!isReschedule ? '<div class="mt-2"><button id="back-to-services" class="btn">← Natrag</button></div>' : ''}
              </div>
          `;
  
          // Back button
          if (!isReschedule) {
              document.getElementById('back-to-services').addEventListener('click', () => {
                  navigateTo('services');
              });
          }

  
          // Slot selection
          container.querySelectorAll('.slot-card').forEach(card => {
              card.addEventListener('click', async () => {
                  // Visual selection
                  container.querySelectorAll('.slot-card').forEach(c => c.classList.remove('selected'));
                  card.classList.add('selected');
  
                  // Reserve the slot
                  try {
                      if (data.rescheduleToken) {
                        // Reschedule - one call does everything (book + confirm)
                        await api.post('/storefront/reservations/reschedule', {
                            timeSlotId: card.dataset.slotId,
                            rescheduleToken: data.rescheduleToken
                        });
                        container.innerHTML = `
                              <div class="success-box">
                                  <h2>✅ Termin premješten</h2>
                                  <p class="mt-2">Vaš novi termin je potvrđen! Potvrda je poslana na email.</p>
                                  <button class="btn btn-primary mt-2" onclick="window.location.href='index.html'">Gotovo</button>
                              </div>
                          `;                        
                      } else {
                        // Normal flow - reserve then go to checkout
                        const reservation = await api.post('/storefront/reservations', {
                            timeSlotId: card.dataset.slotId
                        });
                          navigateTo('checkout', {
                              ...data,
                              reservationId: reservation.timeSlotId,
                              expiresAt: reservation.expiresAt
                          });
                      }
                  } catch (err) {
                      card.classList.remove('selected');
                      alert(err.reason || 'Termin više nije dostupan');
                  }
              });
          });
  
      } catch (err) {
          container.innerHTML = `<p style="color: #dc2626;">Greška pri učitavanju termina</p>`;
      }
  }
  
  function toISODate(date) {
      return date.toISOString().split('T')[0];
  }
  
  function addDays(date, days) {
      const d = new Date(date);
      d.setDate(d.getDate() + days);
      return d;
  }
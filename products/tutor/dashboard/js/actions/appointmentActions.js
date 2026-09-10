import { api } from '../api.js';
  
  // Close an appointment (COMPLETED or NO_SHOW). Follow-up email only on COMPLETED.
  // onDone: callback the caller uses to refresh its own view.
  export async function closeAppointment(appointmentId, outcome, onDone) {
      try {
          await api.post(`/dashboard/appointments/${appointmentId}/close`, {
              outcome,
              sendFollowup: outcome === 'COMPLETED'
          });
          if (onDone) await onDone();
      } catch (err) {
          alert(err.reason || 'Greška pri zatvaranju termina');
      }
  }
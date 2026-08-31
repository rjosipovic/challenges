import { api } from './api.js';
  import { renderServices } from './services.js';
  
  // Load branding
  async function init() {
      try {
          const branding = await api.get('/storefront/config/branding');
          if (branding.name) {
              document.getElementById('brand-name').textContent = branding.name;
              document.getElementById('footer-text').textContent = `© 2026 ${branding.name}`;
              document.title = branding.name;
          }
      } catch (e) {
          // fallback to defaults
      }
  
      const container = document.getElementById('page-container');
      const params = new URLSearchParams(window.location.search);
      const rescheduleToken = params.get('rescheduleToken')

      if (rescheduleToken) {
        // Skip service selection, go directly to availability with reschedule token
        navigateTo('availability', { rescheduleToken });
      } else {
      renderServices(container);

      }
  }
  
  init();
  
  // Export for page navigation
  export function navigateTo(page, data) {
      const container = document.getElementById('page-container');
      switch (page) {
          case 'services':
              import('./services.js').then(m => m.renderServices(container));
              break;
          case 'availability':
              import('./availability.js').then(m => m.renderAvailability(container, data));
              break;
          case 'checkout':
              import('./checkout.js').then(m => m.renderCheckout(container, data));
              break;
      }
  }
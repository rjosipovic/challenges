const BASE_URL = 'http://localhost:8080/api/v1';
  
  async function request(method, path, body = null) {
      const headers = { 'Content-Type': 'application/json' };
      const options = { method, headers };
  
      if (body) {
          options.body = JSON.stringify(body);
      }
  
      const response = await fetch(`${BASE_URL}${path}`, options);
  
      if (!response.ok) {
          const error = await response.text().then(text => text ? JSON.parse(text) : { message: 'Request failed' });
          throw error;
      }
  
      const text = await response.text();
      return text ? JSON.parse(text) : null;
  }
  
  export const api = {
      get: (path) => request('GET', path),
      post: (path, body) => request('POST', path, body),
  };
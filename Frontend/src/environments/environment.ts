export const environment = {
  production: false,
  // Dev też idzie przez gateway (publikowany na 8080 przez docker-compose.dev.yml).
  apiUrl: 'http://localhost:8080',
  articleApiUrl: 'http://localhost:8080',
};

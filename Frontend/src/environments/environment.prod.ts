export const environment = {
  production: true,
  // W Dockerze wszystko idzie przez API Gateway (jeden origin, port 8080).
  apiUrl: 'http://localhost:8080',
  articleApiUrl: 'http://localhost:8080',
};

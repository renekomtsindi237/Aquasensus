import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800']
  }
};

const BASE = __ENV.AQS_BASE_URL || 'http://localhost:8080';

export default function () {
  const sante = http.get(`${BASE}/api/v1/health`);
  check(sante, { 'health 200': (r) => r.status === 200 });
  const carte = http.get(`${BASE}/api/v1/water-points/map`);
  check(carte, { 'carte 200': (r) => r.status === 200 });
  sleep(1);
}

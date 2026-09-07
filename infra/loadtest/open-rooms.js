// k6 load test for Sprint 3 T-M181.
//
// Goal: open 50 concurrent Daily.co rooms (POST /api/v1/sessions/{bookingId}/room)
// and verify p95 latency stays under the SLO documented in
// docs/MANUAL_E2E_SPRINT3.md §7.
//
// Usage:
//   BASE_URL=https://staging.skillseed.app \
//   ACCESS_TOKEN=eyJhbGciOi... \
//   k6 run --vus 50 --duration 5m infra/loadtest/open-rooms.js
//
// `ACCESS_TOKEN` must belong to a user that is the teacher on every
// `BOOKING_ID` in the CSV. The simplest setup is to provision 50
// confirmed bookings that share a single teacher and read them from
// BOOKING_IDS (comma-separated) at the top of the script.

import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || '';
const BOOKING_IDS = (__ENV.BOOKING_IDS || '').split(',').filter(Boolean);

if (!ACCESS_TOKEN) {
  throw new Error('ACCESS_TOKEN env var is required');
}
if (BOOKING_IDS.length === 0) {
  throw new Error('BOOKING_IDS env var must list at least one booking');
}

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 50 },
    { duration: '5m', target: 50 },
    { duration: '30s', target: 0 }
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01']
  }
};

export default function () {
  const bookingId = BOOKING_IDS[(__VU - 1) % BOOKING_IDS.length];
  const res = http.post(
    `${BASE_URL}/api/v1/sessions/${bookingId}/room`,
    null,
    {
      headers: {
        Authorization: `Bearer ${ACCESS_TOKEN}`,
        'Content-Type': 'application/json'
      },
      tags: { name: 'openRoom' }
    }
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
    'roomUrl present': (r) => {
      try {
        return Boolean(JSON.parse(r.body).roomUrl);
      } catch {
        return false;
      }
    }
  });

  sleep(1);
}

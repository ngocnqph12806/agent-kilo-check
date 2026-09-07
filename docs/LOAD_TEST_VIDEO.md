# Load Test — 50 Concurrent Video Sessions (Sprint 3, T-M181)

> **Goal:** Validate that the backend can sustain 50 simultaneous
> in-flight video sessions (auth, room creation, meeting token minting,
> chat over WebSocket) without breaking the 99.5 % availability SLO.
>
> Daily.co is responsible for the media plane — this test focuses on the
> **control plane** of SkillSeed (room + token endpoint, WebSocket
> fan-out, wallet release after the session).

## 1. Tooling

- **k6** with `xk6-websockets` extension — the simplest stack for HTTP +
  WebSocket load. Single binary, easy to run in CI.
- **Optional:** `grafana/k6` Docker image (`grafana/k6:latest`) for a
  fully reproducible containerised run.

## 2. Scenarios

| Scenario | VUs | Duration | Notes |
|----------|-----|----------|-------|
| Smoke | 5  | 30 s | Sanity check. |
| Steady | 50 | 5 min  | The acceptance target — 50 concurrent sessions. |
| Spike  | 100| 1 min  | Burst from 0 to 100 VUs in 10 s. |
| Soak   | 50 | 1 h    | Catches memory leaks. |

## 3. k6 script outline (`load/video-sessions.js`)

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import ws from 'k6/ws';
import { Trend, Rate } from 'k6/metrics';

const roomLatency = new Trend('room_create_ms');
const chatLatency = new Trend('chat_e2e_ms');
const wsConnect = new Rate('ws_connect_ok');

export const options = {
  scenarios: {
    steady: { executor: 'constant-vus', vus: 50, duration: '5m' }
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    room_create_ms: ['p(95)<400'],
    chat_e2e_ms:   ['p(95)<300'],
    ws_connect_ok: ['rate>0.99']
  }
};

const BASE = __ENV.SKILLSEED_API || 'http://localhost:8080';

export function setup() {
  // Pre-create 50 teacher accounts + 50 learner accounts + 50 confirmed
  // bookings. Out of band: a small JUnit class or a dedicated `seed-load`
  // script. Returns the credentials.
  return JSON.parse(open('./fixtures/credentials.json'));
}

export default function (data) {
  const teacher = data.teachers[__VU % data.teachers.length];
  const learner = data.learners[__VU % data.learners.length];
  const bookingId = data.bookings[__VU % data.bookings.length];

  // 1. Each VU logs in as the learner (refresh tokens are short, so the
  //    auth round-trip is part of the load we want to measure).
  const login = http.post(`${BASE}/api/v1/auth/login`,
    JSON.stringify({ email: learner.email, password: learner.password }),
    { headers: { 'Content-Type': 'application/json' } });
  check(login, { 'login 200': (r) => r.status === 200 });
  const token = login.json('accessToken');

  // 2. Join the room.
  const roomStart = Date.now();
  const room = http.post(`${BASE}/api/v1/sessions/${bookingId}/room`,
    null, { headers: { Authorization: `Bearer ${token}` } });
  roomLatency.add(Date.now() - roomStart);
  check(room, { 'room 200': (r) => r.status === 200 });
  wsConnect.add(room.status === 200);

  // 3. Subscribe to chat and post 5 messages.
  const url = BASE.replace(/^http/, 'ws') + '/ws/sessions';
  ws.connect(url, { headers: { Authorization: `Bearer ${token}` } }, (socket) => {
    socket.on('open', () => {
      socket.send(JSON.stringify({
        command: 'SUBSCRIBE',
        destination: `/topic/sessions/${bookingId}`
      }));
    });
    socket.on('message', (data) => {
      chatLatency.add(Date.now() - sentAt);
    });
    socket.setTimeout(() => {
      for (let i = 0; i < 5; i++) {
        sentAt = Date.now();
        socket.send(JSON.stringify({
          command: 'SEND',
          destination: `/app/sessions/${bookingId}/chat`,
          body: JSON.stringify({ text: `hello ${i}` })
        }));
        sleep(0.5);
      }
      socket.close();
    }, 1000);
  });
}
```

## 4. Pass criteria

| Metric | Target | Why |
|--------|--------|-----|
| `http_req_failed` | < 1 % | Backend stays up. |
| `room_create_ms` p95 | < 400 ms | Operator-acceptable join latency. |
| `chat_e2e_ms` p95    | < 300 ms | Perceptually real-time. |
| `ws_connect_ok`      | > 99 % | STOMP handshake must not fail. |
| Memory (backend)     | < 70 % of container limit | No leaks over the soak run. |
| Daily.co minutes     | 50 VUs × 5 min = 250 min | Within free tier (2 000 min/mo). |

## 5. Running locally

```bash
# 1. Boot the stack
docker compose up -d

# 2. Seed 50 confirmed bookings (see tests/load/SeedLoadTest.java)
docker compose exec backend mvn -Dtest=SeedLoadTest test

# 3. Start the run
docker run --rm -i --network=host \
  -v "$PWD/load:/scripts" \
  grafana/k6:latest run /scripts/video-sessions.js
```

## 6. Interpreting results

- **Failed login / 429:** rate limiter kicked in — either reduce VUs or
  tune `notification.login.rate-limit.*` (out of scope for Sprint 3).
- **Room create > 1 s p95:** Daily.co is the bottleneck — check the
  `x-daily-request-id` response header in the backend log to confirm.
- **WS drop > 1 %:** check that the load balancer is sticky on the
  WebSocket upgrade. Spring's STOMP broker is in-memory, so requests
  need to land on the same backend instance after the upgrade.

## 7. CI integration

Add a job in `.github/workflows/ci.yml` that runs the **smoke** scenario
on every PR (5 VUs, 30 s) and the **steady** scenario nightly. Pass / fail
gates the same `thresholds` as in §4.

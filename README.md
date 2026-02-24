# literate-memory

## Linktree Clone: Backend + Frontend Plan

This document outlines a production-oriented architecture and delivery plan for a Linktree-style product with the core features users expect.

## 1) Product goals and core features

### Must-have Linktree-like features
- Public profile page with:
  - avatar, display name, bio, theme/branding
  - multiple links/cards (title, URL, optional thumbnail/icon)
  - support for social icons and contact buttons
- Link management dashboard:
  - create, edit, delete, reorder links (drag and drop)
  - enable/disable links and schedule publish windows
- Analytics:
  - total views, unique visitors, per-link click counts
  - referrer, device, country (high-level)
  - date range filtering and chart visualizations
- Customization:
  - theme presets + custom colors/fonts/background
  - custom profile slug (`yourapp.com/username`)
- Authentication & account management:
  - email/password and OAuth (Google/Apple)
  - password reset and verified email
- Basic trust & safety:
  - URL validation and malicious URL checks
  - rate limiting + bot mitigation on public page traffic

### Stretch features (phase 2+)
- Link thumbnails auto-fetch, rich link previews
- A/B link ordering, CTA experiments
- Tip jar / monetization links
- Custom domains (`links.creator.com`)
- Team/workspace accounts

## 2) Recommended tech stack

### Frontend
- **Framework**: React + Vite + TypeScript
- **UI**: Tailwind CSS + component library (shadcn/ui or Radix primitives)
- **State/data**: React Query (server state), Zustand (local UI state where needed)
- **Charts**: Recharts or Nivo for analytics dashboards

### Backend
- **API**: Java Spring Boot (REST API)
- **Database**: MongoDB
- **Data access**: Spring Data MongoDB
- **Cache/queue**: Redis + queue worker for events/async analytics processing
- **Object storage**: S3-compatible storage for avatars/background assets
- **Auth**: Spring Security + JWT sessions + refresh tokens, OAuth integration

### Infrastructure
- Dockerized services
- Reverse proxy/CDN (Cloudflare)
- Observability: OpenTelemetry + Grafana/Loki stack
- CI/CD: GitHub Actions with lint/test/build/deploy pipelines

## 3) High-level architecture

### Services
1. **Web App (React + Vite)**
   - Public profile rendering for slug pages (CSR with optional pre-rendering for SEO-critical routes)
   - Authenticated dashboard UI
2. **API Service**
   - User, profile, links, analytics query endpoints
   - Admin/safety endpoints
3. **Analytics Pipeline**
   - Click/view ingestion endpoint writes to queue
   - Worker aggregates events into summary tables
4. **Data Layer**
   - MongoDB for transactional and aggregate/event data
   - Redis for caching, rate limiting counters, queues

### Core data model (simplified collections)
- `users`: _id, email, password_hash, provider, created_at
- `profiles`: _id, user_id, slug, title, bio, avatar_url, theme_json
- `links`: _id, profile_id, title, url, is_enabled, position, starts_at, ends_at
- `profile_views`: _id, profile_id, timestamp, country, device, referrer
- `link_clicks`: _id, link_id, profile_id, timestamp, country, device, referrer
- `daily_analytics`: profile_id/link_id + date + aggregated metrics
- Suggested indexes: unique on `profiles.slug`, compound on analytics collections (`profile_id`, `timestamp`) and (`link_id`, `timestamp`)

## 4) API design (REST-first)

### Public
- `GET /p/:slug` → profile + active links + theme
- `POST /events/view` → track profile view
- `POST /events/click` → track link click

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/oauth/:provider`
- `POST /auth/refresh`
- `POST /auth/logout`

### Dashboard
- `GET /me/profile`
- `PATCH /me/profile`
- `POST /me/links`
- `PATCH /me/links/:id`
- `DELETE /me/links/:id`
- `PATCH /me/links/reorder`
- `GET /me/analytics?from=&to=`

### Platform/safety
- `POST /safety/url-check`
- `GET /health`

## 5) Frontend information architecture

### Routes
- `/` marketing landing page
- `/login`, `/register`, `/forgot-password`
- `/dashboard`
  - `/dashboard/links`
  - `/dashboard/appearance`
  - `/dashboard/analytics`
  - `/dashboard/settings`
- `/:slug` public profile

### Key UI components
- Link editor list with drag-and-drop ordering
- Theme customizer panel with live preview
- Public profile renderer (mobile-first)
- Analytics cards + time-series charts + top links table

## 6) Security, privacy, and abuse prevention

- Enforce strict input validation (Jakarta Bean Validation on DTOs)
- Sanitize text fields and prevent stored XSS
- Validate and normalize outgoing URLs
- Apply per-IP and per-slug rate limiting
- Store passwords with Argon2/bcrypt via Spring Security and strong policy
- Encrypt sensitive tokens/secrets at rest
- Add bot filtering and suspicious click detection rules
- GDPR-ready data export/delete endpoints (phase 2)

## 7) Performance strategy

- Fast-loading public slug pages with CDN caching; optional static pre-render for top profiles
- Edge caching for profile pages
- Precompute analytics rollups (hourly/daily)
- MongoDB indexes on slug/profile_id/timestamp/link_id for query performance
- Cursor pagination for large analytics event tables

## 8) Delivery roadmap

### Phase 1 (MVP: 4-6 weeks)
- Auth + profile + link CRUD + public page rendering
- Basic theming
- View/click tracking + basic dashboard analytics

### Phase 2 (2-4 weeks)
- Advanced analytics dimensions
- URL safety checks, stronger abuse prevention
- Better onboarding and templates

### Phase 3
- Custom domains, monetization widgets, team features

## 9) Suggested sprint breakdown

### Sprint 1
- Repo scaffolding, auth, base schema, profile CRUD

### Sprint 2
- Link CRUD + reorder + public profile route

### Sprint 3
- Event ingestion + analytics dashboard

### Sprint 4
- Theming polish, security hardening, performance tuning, launch checklist

## 10) Definition of done (MVP)

- User can sign up and create a profile slug
- User can add/reorder links and publish profile
- Public page loads quickly and is mobile-friendly
- Click/view analytics are visible within dashboard
- Core security checks and rate limits are active
- CI pipeline passes lint, tests, and build

---

## Sprint 1 implementation (now included in this repository)

Implemented backend scaffold in Java + Spring Boot with MongoDB for Sprint 1:
- Authentication endpoints: `POST /auth/register`, `POST /auth/login`
- Profile CRUD for current user (initial upsert + read):
  - `GET /me/profile`
  - `PATCH /me/profile`
- Base schema/collections:
  - `users` with unique email
  - `profiles` with unique `userId` and unique `slug`
- Security setup:
  - Spring Security stateless configuration
  - JWT-based auth filter
  - BCrypt password hashing
- Validation and error handling:
  - Jakarta Bean Validation on DTOs
  - Global API error handler
- Health endpoint:
  - `GET /health`

### Run locally
1. Set environment variables (optional defaults exist):
   - `MONGODB_URI` (default: `mongodb://localhost:27017/linktree_clone`)
   - `JWT_SECRET`
   - `JWT_EXPIRATION_SECONDS`
2. Run app:
   - `mvn spring-boot:run`

> Note: build/test may require Maven repository access to download dependencies.


## Sprint 2 implementation (now included in this repository)

Implemented backend features for Sprint 2:
- Link CRUD endpoints for authenticated users:
  - `GET /me/links`
  - `POST /me/links`
  - `PATCH /me/links/{id}`
  - `DELETE /me/links/{id}`
- Link reorder endpoint:
  - `PATCH /me/links/reorder` (validates complete + unique ordering payload)
- Public profile route:
  - `GET /p/{slug}` returns public profile information and active links
- Link scheduling/enabled filtering:
  - Public response includes only enabled links in valid publish windows
- MongoDB indexes for links:
  - compound unique index on (`profileId`, `position`)
  - query index on (`profileId`, `isEnabled`)


## Sprint 3 implementation (now included in this repository)

Implemented backend features for Sprint 3:
- Event ingestion endpoints:
  - `POST /events/view`
  - `POST /events/click`
- Analytics dashboard endpoint:
  - `GET /me/analytics?from=<ISO>&to=<ISO>`
- Captured event dimensions:
  - profile/link ids, timestamp, visitor id, device type, referrer, country
- Analytics aggregations returned to dashboard:
  - total views, unique visitors, total clicks
  - daily views/clicks time series
  - top clicked links (top 5)
- Added MongoDB indexes for analytics query patterns on profile/link/time and visitor dimensions.


## Sprint 4 implementation (now included in this repository)

Implemented backend hardening and launch-oriented improvements for Sprint 4:
- Security hardening:
  - `POST /safety/url-check` endpoint to validate outbound links and block local/private targets
  - Rate limiting filter for high-traffic public surfaces (`/p/**`, `/events/**`, `/safety/**`)
- Performance tuning:
  - Enabled Spring caching
  - Cached public profile responses and public link resolution
  - Cache eviction on profile and link mutations to keep public data fresh
- Readiness:
  - Added unit tests for URL safety and rate limiting behavior
  - Maintained previous sprint APIs while introducing hardening layers

## Frontend implementation (React + Vite)

A new frontend app now exists in `frontend/` with:
- Routing for:
  - `/`, `/login`, `/register`
  - `/dashboard/links`, `/dashboard/appearance`, `/dashboard/analytics`, `/dashboard/settings`
  - `/:slug` public profile
- API integration with the backend endpoints for auth, profile, links, analytics, and public profile rendering.

### Run frontend locally
1. `cd frontend`
2. `npm install`
3. `npm run dev`

Optional env:
- `VITE_API_BASE_URL` (default: `http://localhost:8080`)

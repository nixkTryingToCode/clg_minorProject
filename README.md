# Recruitment Platform — Backend (Java / Spring Boot)

AI-powered recruitment platform: JWT auth, candidate/recruiter profiles, companies
and job CRUD, with a React frontend.

## Quick links

- **Frontend UI:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **API docs (Swagger):** http://localhost:8080/docs

## Run it locally

1. **Start the backend** (in a terminal inside this folder):

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   Uses PostgreSQL (`recruitment` DB). For no-Docker/embedded H2 instead:

   ```powershell
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
   ```

2. **Start the frontend** (in a terminal inside the `recruitment-frontend` folder):

   ```powershell
   npm install
   npm run dev
   ```

3. Open http://localhost:3000 — register as a CANDIDATE or RECRUITER and start using it.

## Stop the servers

- **Running in a terminal:** press `Ctrl+C` in that window.
- **Running in the background** (started detached / from an IDE): kill by PID.
  Currently running backend:

  ```powershell
  taskkill /PID 25624 /F
  ```

  Or auto-find whatever is on port 8080/3000 and kill it:

  ```powershell
  $p = (Get-NetTCPConnection -LocalPort 8080 -State Listen).OwningProcess; if ($p) { taskkill /F /PID $p } else { "nothing on 8080" }
  $p = (Get-NetTCPConnection -LocalPort 3000 -State Listen).OwningProcess; if ($p) { taskkill /F /PID $p } else { "nothing on 3000" }
  ```

> **Only ever run ONE backend instance at a time** — every instance needs port
> 8080, and a second one exits with `APPLICATION FAILED TO START ... Port 8080
> was already in use`.

## Try the API

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"you@example.com","password":"password123","name":"You","role":"CANDIDATE"}'
```

You get a JWT back — use it as `Authorization: Bearer <token>`.

## API surface

- `POST /api/auth/register`, `POST /api/auth/login`
- `GET /api/users/me` (any authenticated user)
- **Candidates** (`hasRole('CANDIDATE')`): `GET/PUT /api/candidates/me/profile`
- **Recruiters** (`hasRole('RECRUITER')`): `GET/PUT /api/recruiters/me/profile`,
  `GET/PUT/POST /api/recruiters/companies...`, `GET /api/recruiters/me/jobs`
- **Jobs**
  - Public: `GET /api/jobs?location=&q=&page=&size=` (only PUBLISHED),
    `GET /api/jobs/{id}`
  - Recruiter-only: `POST /api/jobs`, `PUT /api/jobs/{id}`,
    `DELETE /api/jobs/{id}`,
    `PATCH /api/jobs/{id}/status?status=PUBLISHED|UNPUBLISHED|CLOSED`
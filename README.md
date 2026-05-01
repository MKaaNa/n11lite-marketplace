# N11Lite Marketplace

Sprint 0 monorepo skeleton for an n11-inspired marketplace final project.

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

## PostgreSQL

```bash
docker compose up -d postgres
```

Local development database:

- Database: `n11_marketplace`
- User: `n11_dev_user`
- Password: `n11_dev_password`

## Local Email Testing

Mailpit is used for local email testing.

- SMTP runs on `localhost:1025`
- Web inbox is available at http://localhost:8025
- Register and login verification emails can be checked from the Mailpit UI

## CI/CD and Deployment

Basic CI/CD and deployment notes are in [docs/deployment.md](docs/deployment.md).

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Mailpit UI: http://localhost:8025


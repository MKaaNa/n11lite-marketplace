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


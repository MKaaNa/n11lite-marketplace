# CI/CD and Deployment Notes

This project uses a simple local development and deployment plan. It is not a live production deployment yet.

## Local Docker Compose

Docker Compose starts the local services used by the backend:

- PostgreSQL for the application database
- Mailpit for local email testing

```powershell
docker compose up -d postgres mailpit
```

Backend local run:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend local run:

```powershell
cd frontend
npm install
npm run dev
```

Mailpit inbox:

```text
http://localhost:8025
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Jib Backend Image

The backend uses Jib to build a container image without writing a Dockerfile. Jib packages the Spring Boot application and creates the image from Maven.

Windows:

```powershell
cd backend
.\mvnw.cmd jib:dockerBuild
```

Linux/macOS:

```bash
cd backend
./mvnw jib:dockerBuild
```

The configured image name is:

```text
n11lite-marketplace/backend
```

No registry credentials are stored in the project.

## GitHub Actions

The CI workflow runs on pushes and pull requests to `main`.

It checks:

- Backend tests with Java 21
- Frontend production build with Node

This helps catch compile, test, and build errors before merging changes.

## Jenkins Comparison

Jenkins can run the same backend and frontend steps with a Jenkinsfile. It is useful when a company already has Jenkins agents, plugins, and internal deployment jobs.

For this project, GitHub Actions is enough because the code is already on GitHub and the pipeline is small.

## AWS Elastic Beanstalk + RDS Plan

A possible deployment plan:

- Deploy backend to AWS Elastic Beanstalk
- Use PostgreSQL on AWS RDS
- Configure database, JWT, mail, and Iyzico values with environment variables
- Host frontend separately later, for example with S3/CloudFront or another static hosting service

Important environment variables would include database connection values, JWT secret, mail settings, and Iyzico API keys.

## Slack Notification Plan

GitHub Actions can send build or deployment status to Slack with a webhook.

Example use:

- Notify Slack when CI passes
- Notify Slack when CI fails
- Notify Slack after deployment

Real Slack webhook URLs should be stored as GitHub secrets, not committed to the repository.

## Development Setup
<!-- TODO: Improve the README -->

This guide covers running the application in **development mode** — where the Spring Boot backend runs directly on your
machine via your **IDE**, and Docker is used only for infrastructure services, like - PostgreSQL, Redis etc.

> **When to use this:** Active feature development, debugging, or when you need hot-reload and full IDE support.

## Architecture Overview

```
Machine
├── IDE (IntelliJ / VS Code)     -> Spring Boot backend (port 8080)
├── Browser / npm run dev        -> React frontend      (port 5173)
└── Docker
    ├── postgres        -> PostgreSQL + PgVector (port 5432)
    └── redis           -> Redis                 (port 6379)
```

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- Java 25 (with your IDE configured)
- Node.js 22+

### Steps

#### Step 1: Configure Environment Variables

The backend reads environment variables from your IDE run configuration (not a `.env` file by default).

1. Copy the template:
   ```shell
   cp docker/docker-compose/.env.example .env.local.reference
   ```
2. Open your IDE run configuration for the Spring Boot application.
3. Add the required variables from `.env.example` as environment variables.

> See [`docker/docker-compose/.env.example`](../.env.example) for the full list and descriptions.

**Minimum required variables for local dev:**

| Variable                          | Notes                                         |
|-----------------------------------|-----------------------------------------------|
| `SPRING_PROFILES_ACTIVE`          | Set to `dev`                                  |
| `SERVER_PORT`                     | `8080`                                        |
| `GOOGLE_CLIENT_ID`                | Required for Google login                     |
| `OPENAI_API_KEY`                  | Required for AI features (optional otherwise) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Required for email features                   |

> PostgreSQL and Redis connection details are pre-configured in the `dev` Spring profile to point to the local Docker
> containers.

## Step 2: Start Infrastructure Services

From the project root:

```shell
cd docker/docker-compose/local
docker-compose up -d
```

This starts:

- **PostgreSQL (PgVector)** on `localhost:5432`
- **Redis** on `localhost:6379`

Verify containers are healthy:

```shell
docker-compose ps
```

## Step 3: Start the Backend

Run or debug the Spring Boot application from your IDE.

Once started, the API and Swagger docs are available at:

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

---

## Step 4: Start the Frontend

From the project root:

```shell
cd frontend
npm install # Only needed on first run or after dependency changes
npm run dev
```

The frontend dev server starts at:

```
http://localhost:5173
```

> For local dev, Vite proxies API calls to `localhost:8080`. You do **not** need `VITE_*` variables in docker-compose —
> set them in `frontend/.env.local` instead.

## Final Checklist

| Service                                 | Status                 |
|-----------------------------------------|------------------------|
| Docker containers (`postgres`, `redis`) | ✅ Running              |
| Spring Boot backend                     | ✅ Running on port 8080 |
| React frontend                          | ✅ Running on port 5173 |

Once everything is up, open `http://localhost:5173`, create an account, and start exploring. 🚀

## Stopping Services

```shell
# Stop infrastructure containers
cd docker/docker-compose/local
docker-compose down

# To also remove persisted data volumes
docker-compose down -v
```
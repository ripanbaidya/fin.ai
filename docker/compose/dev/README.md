## Development Setup Guide

Welcome! This guide walks you through setting up your local environment for **active feature development and debugging**.

In this setup, your Spring Boot backend runs directly inside your **IDE** (giving you full hot-reload and debugging capabilities), while infrastructure services (PostgreSQL, Redis) run via Docker.

### Prerequisites

Before you begin, ensure you have the following installed and running:

- **Docker Desktop** (Make sure the application is running)
- **Java 25** (Configured in your IDE of choice)
- **Node.js** (Version `22+`)

### Step-by-Step Setup

#### Step 1: Start Infrastructure Services

Spin up the required database and cache containers using Docker Compose.

1. Open your terminal and navigate to the development compose directory:

   ```bash
   cd docker/compose/dev
   ```

2. Start the services in detached mode:

   ```bash
   docker compose up -d
   ```

3. Verify that the containers (`PostgreSQL` and `Redis`) are running successfully:
   ```bash
   docker ps
   ```

#### Step 2: Configure & Run the Backend

The Spring Boot backend reads its configuration from **IDE run configurations** rather than a `.env` file by default.

1. **Reference Configuration:** Look at `.env.example` inside the `backend` folder as a guide for required environment variables.
2. **Firebase Setup:** Ensure your `credentials.json` file is placed in the correct classpath location for Firebase Cloud Messaging (FCM) notifications.
3. **Run:** Start the backend application directly from your IDE or via your terminal.

**Note** - [Click here to see complete guidence to configure Firebase Cloud Messaging(FCM)](/public/docs/Migrating%20FCM%20for%20Notification.md)

#### Step 3: Configure & Run the Frontend

Get the client-side application running locally.

1. From the project root, navigate to the frontend directory:

   ```bash
   cd frontend
   ```

2. Create your local environment configuration file:

   ```bash
   cp .env.example .env
   ```

   _(Update any specific environment variables inside the new `.env` file if needed)_

3. Install dependencies and start the development server:
   ```bash
   npm install && npm run dev
   ```

#### Step 4: Verify Your Setup

Once everything is up and running, you can access your local application:

- Swagger/OpenAPI Documentation - http://localhost:8080/api/v1/swagger-ui/index.html
- Frontend Application - http://localhost:5173/

> 🎉 **You're all set!** Open the frontend URL, create an account, and start exploring.

### Stopping Services

When you are done working, you can shut down the infrastructure containers:

- **Stop containers (preserve data):**

  ```bash
  docker compose down
  ```

- **Stop containers and delete volumes (clears local database/cache data):**
  ```bash
  docker compose down -v
  ```
